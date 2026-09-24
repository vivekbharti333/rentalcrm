package com.datfusrental.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.datfusrental.constant.Constant;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.object.request.LeadRequestObject;
import com.datfusrental.object.request.Request;
import com.datfusrental.object.response.GenricResponse;
import com.datfusrental.object.response.Response;
import com.datfusrental.services.BookingUpgradeService;

@RestController
@CrossOrigin(origins = "*")
public class BookingUpgradeController {
    @Autowired private BookingUpgradeService service;

    @PostMapping("getBookingUpgradeContext")
    public Response<Map<String, Object>> context(@RequestBody Request<LeadRequestObject> request) {
        GenricResponse<Map<String, Object>> response = new GenricResponse<>();
        try {
            return response.createSuccessResponse(service.context(request.getPayload().getId()), Constant.SUCCESS_CODE);
        } catch (BizException e) {
            return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
        } catch (Exception e) {
            return response.createErrorResponse(Constant.INTERNAL_SERVER_ERR, "Unable to load booking upgrades.");
        }
    }

    @PostMapping("upgradeBooking")
    public Response<LeadDetails> upgrade(@RequestBody Request<LeadRequestObject> request) {
        GenricResponse<LeadDetails> response = new GenricResponse<>();
        try {
            return response.createSuccessResponse(service.upgrade(request.getPayload()), Constant.SUCCESS_CODE);
        } catch (BizException e) {
            return response.createErrorResponse(Constant.BAD_REQUEST_CODE, e.getMessage());
        } catch (Exception e) {
            return response.createErrorResponse(Constant.INTERNAL_SERVER_ERR, "Unable to save upgrade. Reopen the booking to check whether it was saved.");
        }
    }
}
