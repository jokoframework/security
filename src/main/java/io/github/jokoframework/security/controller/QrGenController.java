package io.github.jokoframework.security.controller;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Controller
public class QrGenController {

    private TwoFactorAuthUtil code = new TwoFactorAuthUtil();

    @GetMapping(value = "qrcode")
    public void getQrCode(HttpServletResponse response) throws GeneralSecurityException, IOException {
        response.addHeader("Content-Type", "image/png");
        String number = code.generateBase32Secret();
        System.out.printf("%n%n%n%s%n%n%n", number);
        String OTP = code.generateCurrentNumber(number);
        System.out.printf("%n%n%n%s%n%n%n", OTP);
        File rah = QRCode.from(qrImageUrl("rah", number)).to(ImageType.PNG).file();

        IOUtils.copy(new FileInputStream(rah),response.getOutputStream());

    }

    public String qrImageUrl(String keyId, String secret) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("otpauth://totp/").append(keyId).append("%3Fsecret%3D").append(secret);
        return sb.toString();
    }
}
