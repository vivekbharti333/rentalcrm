package com.datfusrental.services;

import java.lang.reflect.Field;
import com.datfusrental.entities.OtpDetails;
import com.datfusrental.entities.User;
import com.datfusrental.helper.OtpHelper;
import com.datfusrental.helper.UserHelper;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.request.UserRequestObject;
import org.springframework.security.crypto.bcrypt.BCrypt;
import junit.framework.TestCase;

public class UserPasswordResetTest extends TestCase {
    public void testVerifiedOtpUsesLoginIdAndUpdatesLoginHash() throws Exception {
        User user = new User();
        user.setLoginId("login-id");
        user.setAlternateMobile("8982717649");
        OtpDetails otp = new OtpDetails();
        otp.setStatus("VERIFIED");
        UserService service = service(user, otp);
        service.changeUserPassword(request());
        assertEquals("USED", otp.getStatus());
        assertTrue(BCrypt.checkpw("new-password", user.getSalt()));
        try {
            service.changeUserPassword(request());
            fail("Used OTP must not reset the password again");
        } catch (BizException expected) {
            assertEquals("OTP not verified. Please verify OTP first", expected.getMessage());
        }
    }

    public void testUnverifiedOtpCannotChangePassword() throws Exception {
        User user = new User();
        OtpDetails otp = new OtpDetails();
        otp.setStatus("INIT");
        try {
            service(user, otp).changeUserPassword(request());
            fail("Unverified OTP must be rejected");
        } catch (BizException expected) {
            assertNull(user.getPassword());
            assertEquals("INIT", otp.getStatus());
        }
    }

    private UserService service(User user, OtpDetails otp) throws Exception {
        UserService service = new UserService();
        inject(service, "userHelper", new UserHelper() {
            @Override public User getUserDetailsByLoginId(String id) { return user; }
            @Override public User UpdateUserDetails(User value) { return value; }
        });
        inject(service, "otpHelper", new OtpHelper() {
            @Override public OtpDetails getOtpDetailsByMobile(String key) {
                assertEquals("login-id", key);
                return otp;
            }
            @Override public OtpDetails updateOtpDetails(OtpDetails value) { return value; }
        });
        return service;
    }

    private Request<UserRequestObject> request() {
        UserRequestObject payload = new UserRequestObject();
        payload.setLoginId("login-id");
        payload.setPassword("new-password");
        Request<UserRequestObject> request = new Request<>();
        request.setPayload(payload);
        return request;
    }

    private void inject(UserService service, String name, Object value) throws Exception {
        Field field = UserService.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(service, value);
    }
}
