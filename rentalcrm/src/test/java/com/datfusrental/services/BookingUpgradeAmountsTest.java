package com.datfusrental.services;

import junit.framework.TestCase;
import org.springframework.beans.BeanUtils;
import com.datfusrental.entities.LeadDetails;
import com.datfusrental.object.request.LeadRequestObject;

public class BookingUpgradeAmountsTest extends TestCase {
    private LeadRequestObject request(long price, long vendor, long paid) {
        LeadRequestObject request = new LeadRequestObject();
        request.setQuantity(1);
        request.setTotalDays(1);
        request.setCompanyRate(price);
        request.setVendorRate(vendor);
        request.setActualAmount(paid);
        return request;
    }
    private LeadDetails booking(long price, long vendor, long paid, boolean gst) {
        LeadDetails lead = new LeadDetails();
        lead.setCategoryTypeName("Car");
        lead.setNeedGstInvoice(gst);
        lead.setQuantity(1);
        lead.setTotalDays(1);
        BookingUpgradeAmounts.calculate(lead, request(price, vendor, paid));
        return lead;
    }
    public void testReportsAddOnlyDifferenceAndPreserveOriginal() {
        LeadDetails old = booking(5000, 3000, 2000, false);
        LeadDetails before = new LeadDetails();
        BeanUtils.copyProperties(old, before);
        LeadDetails next = booking(7000, 4000, 2500, false);
        BookingUpgradeAmounts.makeDifference(next, old);
        assertEquals(before, old);
        assertEquals(2000L, next.getTotalAmount());
        assertEquals(500L, next.getActualAmount());
        assertEquals(1000L, next.getBookingAmount());
        assertEquals(1500L, next.getBalanceAmount());
        assertEquals(7000L, old.getTotalAmount() + next.getTotalAmount());
    }
    public void testGstDifference() {
        LeadDetails old = booking(5000, 3000, 2900, true);
        LeadDetails next = booking(7000, 4000, 4000, true);
        BookingUpgradeAmounts.makeDifference(next, old);
        assertEquals(2360L, next.getGstAmount());
        assertEquals(1360L, next.getBookingAmountWithGst().longValue());
        assertEquals(5900L, next.getUpgradeOldTotal().longValue());
        assertEquals(8260L, next.getUpgradeNewTotal().longValue());
    }
    public void testRepeatedUpgradeUsesPreviousFullPrice() {
        LeadDetails original = booking(5000, 3000, 2000, false);
        LeadDetails current = booking(7000, 4000, 3000, false);
        LeadDetails first = booking(7000, 4000, 3000, false);
        BookingUpgradeAmounts.makeDifference(first, original);
        LeadDetails second = booking(8000, 4500, 3500, false);
        BookingUpgradeAmounts.makeDifference(second, current);
        assertEquals(1000L, second.getTotalAmount());
        assertEquals(8000L, original.getTotalAmount() + first.getTotalAmount() + second.getTotalAmount());
        assertEquals(3500L, original.getActualAmount() + first.getActualAmount() + second.getActualAmount());
    }
    public void testReceivedMarginCrossingPreviousUnpaidBalance() {
        LeadDetails old = booking(5000, 3000, 1000, false);
        LeadDetails next = booking(7000, 4000, 4000, false);
        BookingUpgradeAmounts.makeDifference(next, old);
        assertEquals(2000L, next.getUpgradeReceivedMargin().longValue());
        assertEquals(3000L, Math.min(old.getActualAmount(), old.getBookingAmount()) + next.getUpgradeReceivedMargin());
    }
    public void testActivityChildrenAndDiscount() {
        LeadDetails next = new LeadDetails();
        next.setCategoryTypeName("Activity");
        LeadRequestObject request = request(2000, 1000, 1000);
        request.setQuantity(2);
        request.setKidQuantity(1);
        request.setCompanyRateForKids(1000);
        request.setVendorRateForKids(500);
        request.setDiscount(200);
        next.setQuantity(2);
        next.setKidQuantity(1);
        BookingUpgradeAmounts.calculate(next, request);
        assertEquals(4800L, next.getTotalAmount());
        assertEquals(2300L, next.getBookingAmount());
        assertEquals(3800L, next.getBalanceAmount());
    }
    public void testRejectDowngradeAndReducedPayment() {
        LeadDetails old = booking(5000, 3000, 2000, false);
        try { BookingUpgradeAmounts.makeDifference(booking(4000, 2000, 2000, false), old); fail(); }
        catch (IllegalArgumentException expected) { }
        try { BookingUpgradeAmounts.makeDifference(booking(7000, 4000, 1000, false), old); fail(); }
        catch (IllegalArgumentException expected) { }
    }
    public void testRejectOverflowAndOverpayment() {
        LeadRequestObject request = request(Long.MAX_VALUE, 1, 0);
        request.setQuantity(2);
        LeadDetails next = new LeadDetails();
        next.setCategoryTypeName("Car");
        next.setQuantity(2);
        next.setTotalDays(1);
        try { BookingUpgradeAmounts.calculate(next, request); fail(); }
        catch (ArithmeticException expected) { }
        try { booking(5000, 3000, 6000, false); fail(); }
        catch (IllegalArgumentException expected) { }
    }
}
