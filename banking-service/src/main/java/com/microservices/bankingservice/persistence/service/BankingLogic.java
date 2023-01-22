package com.microservices.bankingservice.persistence.service;

import com.microservices.bankingservice.business.Currency;
import com.microservices.bankingservice.business.Transaction;
import com.microservices.bankingservice.business.User;
import com.microservices.bankingservice.config.Configuration;
import com.microservices.bankingservice.security.RoleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankingLogic {

    @Autowired
    private Configuration configuration;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    private final RoleConverter converter = new RoleConverter();

    @Transactional
    public String getStarterPack(Jwt jwt) {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) converter.convert(jwt);
        int capital = configuration.getBonusStandard();
        String username = jwt.getClaims().get("preferred_username").toString();
        if (userService.findByUsername(username) != null) {
            return "You already got your bonus, go away!";
        }

        String ans;
        ans = "Meh, you are standard, eat your filthy " + capital + " RUB";

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_premium")) {
                capital = configuration.getBonusPremium();
                ans = "OOO, seems like you are a premium user, granted: " + capital + " RUB!";
            }
        }

        long userId = userService.createNewUser(username);
        currencyService.save(new Currency(0, new User(userId), "RUB", capital));
        transactionService.save(new Transaction(0, null, new User(userId),
                "Entry bonus for registration", "RUB", capital, LocalDateTime.now()));
        return ans;
    }
}
