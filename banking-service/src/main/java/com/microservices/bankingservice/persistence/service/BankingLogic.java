package com.microservices.bankingservice.persistence.service;

import com.microservices.bankingservice.business.Currency;
import com.microservices.bankingservice.business.CurrencyConversion;
import com.microservices.bankingservice.business.Transaction;
import com.microservices.bankingservice.business.User;
import com.microservices.bankingservice.business.complex.Response;
import com.microservices.bankingservice.business.complex.ResponseBuilder;
import com.microservices.bankingservice.config.Configuration;
import com.microservices.bankingservice.proxy.CurrencyConversionProxy;
import com.microservices.bankingservice.proxy.LimitsProxy;
import com.microservices.bankingservice.security.RoleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

//TODO redirect unregistered to get bonus

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
    private LimitsProxy limitsProxy;

    private final RoleConverter converter = new RoleConverter();

    public Currency getTest(Jwt jwt) {
        String username = jwt.getClaims().get("preferred_username").toString();
        long id = userService.findByUsername(username).getId();
        return currencyService.findByOwnerAndCharCode(id, "RUB");
    }

    public String getStarterPack(Jwt jwt) {
        int capital = configuration.getBonusStandard();
        String username = jwt.getClaims().get("preferred_username").toString();

        if (userService.findByUsername(username) != null) {
            return "You already got your bonus, go away!";
        }

        String ans;
        ans = "Meh, you are standard, eat your filthy " + capital + " RUB";

        if (checkPrem(jwt)) {
            capital = configuration.getBonusPremium();
            ans = "OOO, seems like you are a premium user, granted: " + capital + " RUB!";
        }

        long userId = userService.createNewUser(username);
        currencyService.save(new Currency(0, new User(userId), "RUB", capital));
        transactionService.save(new Transaction(0, null, new User(userId),
                "Entry bonus for registration", "RUB", capital, LocalDateTime.now()));
        return ans;
    }

    public List<Currency> getWallet(Jwt jwt) {
        String username = jwt.getClaims().get("preferred_username").toString();
        return currencyService.findAllByOwner(userService.findByUsername(username).getId());
    }

    public Response convertValutes(String from, String to, double quantity, Jwt jwt) {
        ResponseBuilder response;

        String username = jwt.getClaims().get("preferred_username").toString();
        User user = userService.findByUsername(username);
        long ownerId = user.getId();

        //checking valute quantity on the account
        response = checkValuteQuantity(from, quantity, ownerId);
        if (response.getStatus() != HttpStatus.OK) return response.build();

        CurrencyConversion conversion = currencyConversionProxy
                .calculateCurrencyConversionFeign(from, to, quantity);
        System.out.println(conversion.toString());

        //checking if fits the limits
        if (conversion.getTo().equals("USD") || conversion.getTo().equals("EUR")) {
            response = checkConvLimit(to, conversion.getTotalCalculatedAmount(), jwt, ownerId);

            if (response.getStatus() != HttpStatus.OK) return response.build();

            response = checkWalletLimit(to, conversion.getTotalCalculatedAmount(), ownerId);
        }

        // building response and updating db-s
        if (response.getStatus() == HttpStatus.OK) {
            LocalDateTime now = LocalDateTime.now();

            Transaction transaction1 = new Transaction(0, null, new User(user.getId()),
                    "Conversion", to, conversion.getTotalCalculatedAmount(), now);
            Transaction transaction2 = new Transaction(0, new User(user.getId()), null,
                    "Conversion", from, -quantity, now);

            double newFrom = currencyService.updateCurrency(ownerId, from, -quantity);
            double newTo = currencyService.updateCurrency(ownerId, to, conversion.getTotalCalculatedAmount());

            if (to.equals("USD")) {
                user.setLastDollarConv(now);
                user.setCurrentDollarConv(conversion.getTotalCalculatedAmount());
            } else if (to.equals("EUR")) {
                user.setLastEuroConv(now);
                user.setCurrentEuroConv(conversion.getTotalCalculatedAmount());
            }
            user.setLastTransaction(now);

            userService.save(user);
            transactionService.save(transaction1);
            transactionService.save(transaction2);

            response.newBalanceFrom(newFrom).newBalanceTo(newTo).from(from).to(to);
            return response.build();
        } else {
            return response.build();
        }
    }

    //TODO add trans limits control

    public ResponseBuilder checkConvLimit(String code, double quantity, Jwt jwt, long ownerId) {
        int daily;

        //getting limits
        if (checkPrem(jwt)) {
            daily = limitsProxy.getLimits(code, "premium");
        } else {
            daily = limitsProxy.getLimits(code, "standard");
        }

        //checking conversion limits
        User user = userService.findById(ownerId);
        if (Objects.equals(code, "USD")) {
            LocalDate now = LocalDate.now();
            LocalDate lastTrans = user.getLastDollarConv().toLocalDate();
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
            LocalDate lastTrans = user.getLastEuroConv().toLocalDate();
            if (now.isAfter(lastTrans)) {
                user.setCurrentEuroConv(0);
            }
            userService.save(user);

            if (user.getCurrentDollarConv() + quantity > daily) {
                return new ResponseBuilder().status(HttpStatus.BAD_REQUEST)
                        .description("Daily EUR convert will be exceeded." +
                                " Daily limit left: " + (daily - user.getCurrentDollarConv()) + " EUR");
            }
        }
        return new ResponseBuilder().status(HttpStatus.OK).description("Success!");
    }

    public ResponseBuilder checkWalletLimit(String code, double quantity, long ownerId) {
        int maximum = limitsProxy.getLimits(code, "max");

        Currency currency = currencyService.findByOwnerAndCharCode(ownerId, code);
        if (currency == null) {
            currency = new Currency();
            currency.setQuantity(0);
        }

        if (currency.getQuantity() + quantity > maximum) {
            return new ResponseBuilder().status(HttpStatus.BAD_REQUEST)
                    .description(String.format("Your limit to hold for %s on your account" +
                                    " is %d, limit will be exceeded for %a. Current balance: %a %s",
                            code, maximum, currency.getQuantity() + quantity - maximum, currency.getQuantity(), code));
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

    public boolean checkPrem(Jwt jwt) {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) converter.convert(jwt);
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_premium")) {
                return true;
            }
        }
        return false;
    }
}
