package com.microservices.bankingservice.persistence.service;

import com.microservices.bankingservice.business.*;
import com.microservices.bankingservice.business.complex.ResponseBuilder;
import com.microservices.bankingservice.config.Configuration;
import com.microservices.bankingservice.proxy.CurrencyConversionProxy;
import com.microservices.bankingservice.proxy.CurrencyExchangeProxy;
import com.microservices.bankingservice.proxy.LimitsProxy;
import com.microservices.bankingservice.security.RoleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class BankingLogic {

    @Autowired
    private Configuration configuration;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyConversionProxy currencyConversionProxy;

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @Autowired
    private LimitsProxy limitsProxy;

    private final RoleConverter converter = new RoleConverter();

    public String getStarterPack(Jwt jwt) {
        int capital = configuration.getBonusStandard();
        String username = jwt.getClaims().get("preferred_username").toString();

        if (userService.findByUsername(username) != null) {
            return "You already got your bonus, go away!\n/banking/menu";
        }

        String ans;
        ans = "Meh, you are standard, eat your filthy " + capital + " RUB.\n/banking/menu";

        if (checkPrem(jwt)) {
            capital = configuration.getBonusPremium();
            ans = "OOO, seems like you are a premium user, granted: " + capital + " RUB!\n/banking/menu";
        }

        long userId = userService.createNewUser(username);
        currencyService.save(new Currency(0, new User(userId), "RUB", capital));
        transactionService.save(new Transaction(0, new User(userId), null,
                "Entry bonus for registration", "RUB", capital, LocalDateTime.now()));
        return ans;
    }

    public ModelAndView getDailyBonus(Jwt jwt, Model model) {
        String username = jwt.getClaims().get("preferred_username").toString();
        if (!checkRegistered(username)) {
            return new ModelAndView("redirect");
        }
        User user = userService.findByUsername(username);
        long userId = user.getId();

        String message;
        if (user.getLastBonus() == null || user.getLastBonus().isBefore(LocalDate.now())) {
            user.setLastBonus(LocalDate.now());
            userService.save(user);
            currencyService.updateCurrency(userId, "RUB", 100000);
            transactionService.save(new Transaction(0, new User(userId), null, "Daily bonus",
                    "RUB", 100000, LocalDateTime.now()));
            message = "Congratz, you got 100000 RUB!";
        } else {
            message = "Nah, you already got your bonus today.";
        }
        model.addAttribute("message", message);
        return new ModelAndView("daily");
    }

    public ModelAndView getWallet(Jwt jwt, Model model) {
        String username = jwt.getClaims().get("preferred_username").toString();
        if (!checkRegistered(username)) {
            return new ModelAndView("redirect");
        }
        List<Currency> currencies = currencyService.findAllByOwner(userService.findByUsername(username).getId());
        model.addAttribute("currencies", currencies);
        return new ModelAndView("wallet");
    }

    public ModelAndView transferValutes(String to, String code, double quantity, Jwt jwt, Model model) {
        String username = jwt.getClaims().get("preferred_username").toString();
        if (!checkRegistered(username)) {
            return new ModelAndView("redirect");
        }

        User user = userService.findByUsername(username);
        long ownerId = user.getId();

        User userTo = userService.findByUsername(to);
        long userToId = userTo.getId();

        // checking tha amount of money on wallet, trans limits
        ResponseBuilder response = checkRoutineLimits(code, quantity, jwt, ownerId);
        if (response.getStatus() != HttpStatus.OK) return errorResponse(response, model);

        //checking limit on extern account
        if (code.equals("USD") || code.equals("EUR")) {
            response = checkWalletLimit(code, quantity, userToId, true);
            if (response.getStatus() != HttpStatus.OK) return errorResponse(response, model);
        }

        // checking daily transfer limit
        response = checkDailyTransLimits(user, code, quantity);
        if (response.getStatus() != HttpStatus.OK) return errorResponse(response, model);

        //building response and creating transactions
        LocalDateTime timeNow = LocalDateTime.now();

        Transaction transactionFrom = new Transaction(0, new User(ownerId), new User(userToId),
                "Transfer to " + to, code, -quantity, timeNow);
        Transaction transactionTo = new Transaction(0, new User(userToId), new User(ownerId),
                "Transfer from " + username, code, quantity, timeNow);

        double newFrom = currencyService.updateCurrency(ownerId, code, -quantity);
        currencyService.updateCurrency(userToId, code, quantity);

        transactionService.save(transactionFrom);
        transactionService.save(transactionTo);

        response.newBalanceFrom(newFrom).from(code);
        model.addAttribute("newBalanceFrom", response.getNewBalanceFrom());
        model.addAttribute("code", response.getFrom());
        return new ModelAndView("transfer");
    }

    public ModelAndView convertValutes(String from, String to, double quantity, Jwt jwt, Model model) {
        String username = jwt.getClaims().get("preferred_username").toString();
        if (!checkRegistered(username)) {
            return new ModelAndView("redirect");
        }
        User user = userService.findByUsername(username);
        long ownerId = user.getId();

        ResponseBuilder response = checkRoutineLimits(from, quantity, jwt, ownerId);
        if (response.getStatus() != HttpStatus.OK) return errorResponse(response, model);

        CurrencyConversion conversion = currencyConversionProxy
                .calculateCurrencyConversionFeign(from, to, quantity);

        //checking if fits the limits
        if (conversion.getTo().equals("USD") || conversion.getTo().equals("EUR")) {
            response = checkConvLimit(to, conversion.getTotalCalculatedAmount(), jwt, ownerId);
            if (response.getStatus() != HttpStatus.OK) return errorResponse(response, model);

            response = checkWalletLimit(to, conversion.getTotalCalculatedAmount(), ownerId, false);
            if (response.getStatus() != HttpStatus.OK) return errorResponse(response, model);
        }

        // building response and updating db-s
        LocalDateTime now = LocalDateTime.now();

        Transaction transaction1 = new Transaction(0, new User(user.getId()), null,
                "Conversion", to, conversion.getTotalCalculatedAmount(), now);
        Transaction transaction2 = new Transaction(0, new User(user.getId()), null,
                "Conversion", from, -quantity, now);

        double newFrom = currencyService.updateCurrency(ownerId, from, -quantity);
        double newTo = currencyService.updateCurrency(ownerId, to, conversion.getTotalCalculatedAmount());

        if (to.equals("USD")) {
            user.setLastDollarConv(now.toLocalDate());
            user.setCurrentDollarConv(user.getCurrentDollarConv() + conversion.getTotalCalculatedAmount());
        } else if (to.equals("EUR")) {
            user.setLastEuroConv(now.toLocalDate());
            user.setCurrentEuroConv(user.getCurrentEuroConv() + conversion.getTotalCalculatedAmount());
        }
        user.setLastTransaction(now.toLocalDate());

        userService.save(user);
        transactionService.save(transaction1);
        transactionService.save(transaction2);

        response.newBalanceFrom(newFrom).newBalanceTo(newTo).from(from).to(to);
        model.addAttribute("newBalanceFrom", response.getNewBalanceFrom());
        model.addAttribute("newBalanceTo", response.getNewBalanceTo());
        model.addAttribute("from", response.getFrom());
        model.addAttribute("to", response.getTo());
        return new ModelAndView("conversion");
    }

    public ResponseBuilder checkRoutineLimits(String code, double quantity, Jwt jwt, long ownerId) {
        ResponseBuilder response;

        //checking valute quantity on the account
        response = checkValuteQuantity(code, quantity, ownerId);
        if (response.getStatus() != HttpStatus.OK) return response;

        //checking trans amount
        response = checkTransMinMax(code, quantity, jwt);
        return response;
    }

    public ResponseBuilder checkDailyTransLimits(User user, String code, double quantity) {
        int limit = limitsProxy.getLimits("trans", "limit");

        if (user.getLastTransaction().isBefore(LocalDate.now())) {
            user.setCurrentTransLimit(0);
        }
        CurrencyConversion conv = currencyConversionProxy.calculateCurrencyConversionFeign(code, "RUB", quantity);

        if (limit < conv.getTotalCalculatedAmount() + user.getCurrentTransLimit()) {
            return new ResponseBuilder().status(HttpStatus.BAD_REQUEST)
                    .description("Your daily transfer limit will be exceeded by:"
                            + (conv.getTotalCalculatedAmount() + user.getCurrentTransLimit() - limit) +
                    "RUB. Come back tomorrow:(");
        }
        user.setCurrentTransLimit(user.getCurrentTransLimit() + conv.getTotalCalculatedAmount());
        user.setLastTransaction(LocalDate.now());
        userService.save(user);
        return new ResponseBuilder().status(HttpStatus.OK).description("Success!");
    }

    public ResponseBuilder checkTransMinMax(String code, double quantity, Jwt jwt) {
        int min = limitsProxy.getLimits("trans", "min");
        int max = checkPrem(jwt) ? limitsProxy.getLimits("trans", "premium") :
                limitsProxy.getLimits("trans", "max");

        CurrencyConversion conversion = currencyConversionProxy.calculateCurrencyConversionFeign(code, "RUB", quantity);
        if (conversion.getTotalCalculatedAmount() < min) {
            return new ResponseBuilder().status(HttpStatus.BAD_REQUEST).description("Transaction amount is lower than limit! Limit: " + min + " RUB.");
        } else if (conversion.getTotalCalculatedAmount() > max) {
            return new ResponseBuilder().status(HttpStatus.BAD_REQUEST).description("Transaction amount is higher than limit! Limit: " + max + " RUB");
        }
        return new ResponseBuilder().status(HttpStatus.OK).description("Success!");
    }

    public ResponseBuilder checkConvLimit(String code, double quantity, Jwt jwt, long ownerId) {
        int daily = checkPrem(jwt) ? limitsProxy.getLimits(code, "premium") :
                limitsProxy.getLimits(code, "standard");

        //checking conversion limits
        User user = userService.findById(ownerId);
        if (Objects.equals(code, "USD")) {
            LocalDate now = LocalDate.now();
            LocalDate lastTrans = user.getLastDollarConv();
            if (now.isAfter(lastTrans)) {
                user.setCurrentDollarConv(0);
            }
            userService.save(user);

            if (user.getCurrentDollarConv() + quantity > daily) {
                return new ResponseBuilder().status(HttpStatus.BAD_REQUEST)
                        .description("Daily USD convert will be exceeded." +
                                " Daily limit left: " + (daily - user.getCurrentDollarConv()) + " USD");
            }
        } else {
            LocalDate now = LocalDate.now();
            LocalDate lastTrans = user.getLastEuroConv();
            if (now.isAfter(lastTrans)) {
                user.setCurrentEuroConv(0);
            }
            userService.save(user);

            if (user.getCurrentEuroConv() + quantity > daily) {
                return new ResponseBuilder().status(HttpStatus.BAD_REQUEST)
                        .description("Daily EUR convert will be exceeded." +
                                " Daily limit left: " + (daily - user.getCurrentEuroConv()) + " EUR");
            }
        }
        return new ResponseBuilder().status(HttpStatus.OK).description("Success!");
    }

    public ResponseBuilder checkWalletLimit(String code, double quantity, long ownerId, boolean extern) {
        int maximum = limitsProxy.getLimits(code, "max");

        Currency currency = currencyService.findByOwnerAndCharCode(ownerId, code);
        if (currency == null) {
            currency = new Currency();
            currency.setQuantity(0);
        }

        if (currency.getQuantity() + quantity > maximum) {
            String description;
            if (extern) {
                description = String.format("The account's limit to hold for %s will be exceeded." +
                        " Operation canceled.", code);
            } else {
                description = String.format("Your limit to hold for %s on your account" +
                                " is %d, limit will be exceeded for %a. Current balance: %a %s",
                        code, maximum, currency.getQuantity() + quantity - maximum, currency.getQuantity(), code);
            }
            return new ResponseBuilder().status(HttpStatus.BAD_REQUEST)
                    .description(description);
        }
        return new ResponseBuilder().status(HttpStatus.OK).description("Success!");
    }

    public ResponseBuilder checkValuteQuantity(String code, double quantity, long ownerId) {
        Currency currencyFrom = currencyService.findByOwnerAndCharCode(ownerId, code);
        if (currencyFrom == null) {
            currencyFrom = new Currency();
            currencyFrom.setQuantity(0);
        }

        if (currencyFrom.getQuantity() < quantity) {
            return new ResponseBuilder().status(HttpStatus.BAD_REQUEST)
                    .description("Insufficient amount of " + code + "on balance. Expected: " + quantity + ", got: " + currencyFrom.getQuantity());

        }
        return new ResponseBuilder().status(HttpStatus.OK).description("Success!");
    }

    public ModelAndView getAllCurrencies(Model model) {
        List<CurrencyValue> values = currencyExchangeProxy.getAllCurrencies();
        model.addAttribute("currencies", values);
        return new ModelAndView("currencies");
    }

    public ModelAndView findAllTransactions(Jwt jwt, Model model) {
        String username = jwt.getClaims().get("preferred_username").toString();
        if (!checkRegistered(username)) {
            return new ModelAndView("redirect");
        }
        User user = userService.findByUsername(username);
        long ownerId = user.getId();

        List<Transaction> transactions = transactionService.findAllTransactions(ownerId);

        model.addAttribute("transactions", transactions);
        return new ModelAndView("transactions");
    }

    public boolean checkPrem(Jwt jwt) {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) converter.convert(jwt);
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_premium")) {
                return true;
            }
        }
        return false;
    }

    public boolean checkRegistered(String username) {
        return userService.findByUsername(username) != null;
    }

    public ModelAndView errorResponse(ResponseBuilder builder, Model model) {
        model.addAttribute("description", builder.getDescription());
        model.addAttribute("status", builder.getStatus().toString());
        return new ModelAndView("myerror");
    }
}
