package com.datfusrental.services;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.datfusrental.entities.OtpDetails;
import com.datfusrental.entities.User;
import com.datfusrental.helper.OtpHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.object.request.OtpRequestObject;
import com.datfusrental.object.request.Request;
import junit.framework.TestCase;

public class OtpServiceTest extends TestCase {
    public void testFreshOtpUsesLoginIdInsteadOfDeliveryNumber() throws Exception {
        assertEquals("OTP verified successfully", verifyWithAge(0).getRespMesg());
    }

    public void testOtpExpiresAfterTenMinutes() throws Exception {
        assertEquals("OTP expired", verifyWithAge(TimeUnit.MINUTES.toMillis(10)).getRespMesg());
    }

    public void testOtpStillValidAfterNineMinutes() throws Exception {
        assertEquals("OTP verified successfully", verifyWithAge(TimeUnit.MINUTES.toMillis(9)).getRespMesg());
    }

    public void testRegenerationResetsAttemptsAndExpiry() {
        OtpDetails details = new OtpDetails();
        details.setOtpCount(5);
        details.setStatus("EXPIRED");
        details.setUpdatedAt(new Date(0));
        long before = System.currentTimeMillis();
        new OtpHelper().getUpdatedOtpDetailsByReqObj(new OtpRequestObject(), details);
        assertEquals(0, details.getOtpCount());
        assertEquals("INIT", details.getStatus());
        assertTrue(details.getUpdatedAt().getTime() >= before);
    }

    private OtpRequestObject verifyWithAge(long age) throws Exception {
        OtpDetails details = new OtpDetails();
        details.setOtp("123456");
        details.setStatus("INIT");
        details.setUpdatedAt(new Date(System.currentTimeMillis() - age));
        OtpService service = new OtpService();
        inject(service, "otpHelper", new OtpHelper() {
            @Override public OtpDetails getOtpDetailsByMobile(String number) {
                assertEquals("login-id", number);
                return details;
            }
            @Override public OtpDetails updateOtpDetails(OtpDetails value) { return value; }
        });
        inject(service, "userHelper", new UserHelper() {
            @Override public User getUserDetailsByLoginId(String loginId) {
                User user = new User();
                user.setAlternateMobile("8982717649");
                return user;
            }
        });
        OtpRequestObject payload = new OtpRequestObject();
        payload.setCustomerMobile("login-id");
        payload.setOtp("123456");
        Request<OtpRequestObject> request = new Request<>();
        request.setPayload(payload);
        return service.verifyOtp(request);
    }

    private void inject(OtpService service, String name, Object value) throws Exception {
        Field field = OtpService.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }
}
