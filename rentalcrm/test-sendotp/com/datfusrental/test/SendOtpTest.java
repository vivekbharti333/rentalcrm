package com.datfusrental.test;

import com.datfusrental.helper.SendWhatsAppTextMessageHelper;
import com.datfusrental.object.response.WhatsAppMessageResponse;
import com.datfusrental.whatsapp.request.TemplateBodyVariableRequest;
import com.datfusrental.whatsapp.request.TemplateButtonVariableRequest;
import com.datfusrental.whatsapp.request.WhatsAppMessageRequestObject;

import java.util.List;

/**
 * Exercises the EXACT current production path of OtpService.sendWhatsAppPassMessage:
 * body variable = OTP, url button (index 0) = OTP, recipient as passed by the service.
 *
 * Usage: java SendOtpTest <recipientMobile>
 */
public class SendOtpTest {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java SendOtpTest <recipientMobile>");
            System.exit(1);
        }
        String mobileNumber = args[0];
        String otp = "483920"; // fixed test value

        TemplateBodyVariableRequest otpVariable = new TemplateBodyVariableRequest();
        otpVariable.setVariableType("text");
        otpVariable.setBodyVariable(otp);

        TemplateButtonVariableRequest otpButton = new TemplateButtonVariableRequest();
        otpButton.setType("url");
        otpButton.setValue(otp);

        WhatsAppMessageRequestObject messageRequest = new WhatsAppMessageRequestObject();
        messageRequest.setMessageTo(mobileNumber);
        messageRequest.setTemplateName("rywcrm_pass");
        messageRequest.setLanguage("en");
        messageRequest.setMsgBodyVariable(List.of(otpVariable));
        messageRequest.setButtonVariable(List.of(otpButton));

        SendWhatsAppTextMessageHelper helper = new SendWhatsAppTextMessageHelper();
        String payload = helper.getTextTemplateParameter(messageRequest);
        System.out.println("PAYLOAD: " + payload);

        WhatsAppMessageResponse res = helper.callSendTemplateTextMessage(payload);
        System.out.println("ERROR MESSAGE : " + res.getErrorMessage());
        System.out.println("ERROR CODE    : " + res.getErrorCode());
        System.out.println("WA ID         : " + res.getWaId());
        System.out.println("MESSAGE ID    : " + res.getMessagesId());
        System.out.println("STATUS        : " + res.getMessageStatus());

        boolean ok = res.getErrorMessage() == null || res.getErrorMessage().isBlank();
        System.out.println(ok ? "RESULT: SENT OK" : "RESULT: SEND FAILED");
        System.exit(ok ? 0 : 2);
    }
}
