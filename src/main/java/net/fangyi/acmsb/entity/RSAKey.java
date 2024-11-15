package net.fangyi.acmsb.entity;

import lombok.Data;

@Data
public class RSAKey {
    private String publicKey;
    private String privateKey;

    public RSAKey(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
