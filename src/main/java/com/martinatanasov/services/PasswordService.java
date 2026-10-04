package com.martinatanasov.services;

import com.password4j.BcryptFunction;
import com.password4j.Password;
import com.password4j.types.Bcrypt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    private static final BcryptFunction BCRYPT = BcryptFunction.getInstance(Bcrypt.A, 10);

    public String encode(String rawPassword) {
        return Password.hash(rawPassword).with(BCRYPT).getResult();
    }

    public boolean matches(String rawPassword, String hash) {
        return Password.check(rawPassword, hash).with(BCRYPT);
    }
}
