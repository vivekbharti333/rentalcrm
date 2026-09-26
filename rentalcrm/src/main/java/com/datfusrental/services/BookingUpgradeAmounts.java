package com.datfusrental.services;

import com.datfusrental.entities.LeadDetails;
import com.datfusrental.object.request.LeadRequestObject;

/** Uses cumulative prices/payments for the snapshot and differences for reporting. */
public final class BookingUpgradeAmounts {
    private BookingUpgradeAmounts() { }

    public static long effectiveTotal(LeadDetails lead) {
        return Boolean.TRUE.equals(lead.getNeedGstInvoice()) ? lead.getGstAmount() : lead.getTotalAmount();
    }

    public static void calculate(LeadDetails next, LeadRequestObject request) {
        boolean vehicle = "Car".equalsIgnoreCase(next.getCategoryTypeName())
                || "Bike".equalsIgnoreCase(next.getCategoryTypeName());
        if (next.getQuantity() <= 0 || (vehicle && next.getTotalDays() < 1) || next.getKidQuantity() < 0
                || request.getCompanyRate() < 0 || request.getVendorRate() < 0
                || request.getCompanyRateForKids() < 0 || request.getVendorRateForKids() < 0
                || request.getDeliveryAmountToCompany() < 0 || request.getDeliveryAmountToVendor() < 0
                || request.getDiscount() < 0 || request.getActualAmount() < 0 || request.getSecurityAmount() < 0) {
            throw new IllegalArgumentException("Invalid upgrade quantities, rates or payment.");
        }
        long total = vehicle
                ? Math.multiplyExact(Math.addExact(Math.multiplyExact(request.getCompanyRate(), next.getTotalDays()),
                    request.getDeliveryAmountToCompany()), next.getQuantity())
                : Math.addExact(Math.multiplyExact(request.getCompanyRate(), next.getQuantity()),
                    Math.multiplyExact(request.getCompanyRateForKids(), next.getKidQuantity()));
        total = Math.subtractExact(total, request.getDiscount());
        long vendor = vehicle
                ? Math.multiplyExact(Math.addExact(Math.multiplyExact(request.getVendorRate(), next.getTotalDays()),
                    request.getDeliveryAmountToVendor()), next.getQuantity())
                : Math.addExact(Math.multiplyExact(request.getVendorRate(), next.getQuantity()),
                    Math.multiplyExact(request.getVendorRateForKids(), next.getKidQuantity()));
        if (total <= 0 || vendor > total) throw new IllegalArgumentException("Upgrade price must cover the vendor amount.");
        long booking = total - vendor;
        long tax = Boolean.TRUE.equals(next.getNeedGstInvoice()) ? Math.multiplyExact(total, 18) / 100 : 0;
        long gross = Math.addExact(total, tax);
        if (request.getActualAmount() > gross) throw new IllegalArgumentException("Paid amount exceeds upgraded total.");
        long paid = request.getActualAmount();
        long bookingWithTax = Math.addExact(booking, tax);
        next.setTotalAmount(total);
        next.setBookingAmount(booking);
        next.setActualAmount(paid);
        next.setBalanceAmount(paid == 0 ? vendor : gross - paid);
        next.setPayToCompany(paid == 0 ? 0 : Math.max(bookingWithTax - paid, 0));
        next.setPayToVendor(paid == 0 ? 0 : Math.max(paid - bookingWithTax, 0));
        next.setGstAmount(tax == 0 && !Boolean.TRUE.equals(next.getNeedGstInvoice()) ? 0 : gross);
        next.setBookingAmountWithGst(bookingWithTax);
        next.setBalanceAmountWithGst(next.getBalanceAmount());
    }

    public static void makeDifference(LeadDetails row, LeadDetails previous) {
        long oldTotal = effectiveTotal(previous);
        long newTotal = effectiveTotal(row);
        if (newTotal <= oldTotal || row.getActualAmount() < previous.getActualAmount()) {
            throw new IllegalArgumentException("Upgrade total must increase and cumulative paid amount cannot decrease.");
        }
        row.setUpgradeOldTotal(oldTotal);
        row.setUpgradeNewTotal(newTotal);
        // Difference of capped margins, not a cap on differences (which undercounts receipts).
        row.setUpgradeReceivedMargin(Math.min(row.getActualAmount(), row.getBookingAmount())
                - Math.min(previous.getActualAmount(), previous.getBookingAmount()));
        row.setTotalAmount(Math.subtractExact(row.getTotalAmount(), previous.getTotalAmount()));
        row.setBookingAmount(Math.subtractExact(row.getBookingAmount(), previous.getBookingAmount()));
        row.setBalanceAmount(Math.subtractExact(row.getBalanceAmount(), previous.getBalanceAmount()));
        row.setActualAmount(Math.subtractExact(row.getActualAmount(), previous.getActualAmount()));
        row.setPayToCompany(Math.subtractExact(row.getPayToCompany(), previous.getPayToCompany()));
        row.setPayToVendor(Math.subtractExact(row.getPayToVendor(), previous.getPayToVendor()));
        row.setSecurityAmount(Math.subtractExact(row.getSecurityAmount(), previous.getSecurityAmount()));
        row.setDiscount(Math.subtractExact(row.getDiscount(), previous.getDiscount()));
        row.setDeliveryAmountToCompany(Math.subtractExact(row.getDeliveryAmountToCompany(), previous.getDeliveryAmountToCompany()));
        row.setDeliveryAmountToVendor(Math.subtractExact(row.getDeliveryAmountToVendor(), previous.getDeliveryAmountToVendor()));
        row.setGstAmount(Boolean.TRUE.equals(row.getNeedGstInvoice()) ? newTotal - oldTotal : 0);
        row.setBookingAmountWithGst(row.getBookingAmountWithGst() - (previous.getBookingAmountWithGst() == null
                ? previous.getBookingAmount() : previous.getBookingAmountWithGst()));
        row.setBalanceAmountWithGst(row.getBalanceAmountWithGst() - (previous.getBalanceAmountWithGst() == null
                ? previous.getBalanceAmount() : previous.getBalanceAmountWithGst()));
    }
}
