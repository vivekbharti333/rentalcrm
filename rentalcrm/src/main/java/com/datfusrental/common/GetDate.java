package com.datfusrental.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.datfusrental.enums.RequestFor;

@Component
public class GetDate {
	
	   private LocalDate localDate = LocalDate.now();
	   private LocalDate nextday;
	   private LocalDate preday;
	   private LocalDate firstDateOfMonth;
	   private LocalDate lastDateOfMonth;
//	   private Date todayDate;
//	   private Date tomorrowDate;
//	   private Date previousDate;
//	   private Date firstDateMonth;
//	   private Date lastDateMonth;

	   public Date driveDate(String requestedFor) {
		    LocalDate today = this.localDate;  // Assuming this.localDate is already set
		    ZoneId zone = ZoneId.systemDefault();

		    switch (requestedFor.toUpperCase()) {
		        case "PREVIOUS_DATE":
		            return Date.from(today.minusDays(1).atStartOfDay(zone).toInstant());

		        case "NEXT_DATE":
		            return Date.from(today.plusDays(1).atStartOfDay(zone).toInstant());

		        case "NEXT_TO_NEXT_DATE":
		            return Date.from(today.plusDays(2).atStartOfDay(zone).toInstant());

		        case "MONTH_FIRST_DATE":
		            return Date.from(today.withDayOfMonth(1).atStartOfDay(zone).toInstant());

		        case "MONTH_LAST_DATE":
		            return Date.from(today.with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay(zone).toInstant());

		        default:
		            return Date.from(today.atStartOfDay(zone).toInstant()); // fallback to today
		    }
		}


}
