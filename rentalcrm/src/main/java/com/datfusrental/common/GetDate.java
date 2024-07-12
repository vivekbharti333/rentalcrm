package com.datfusrental.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.datfusrental.enums.RequestFor;

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
		   Date date = new Date();
//		   if(requestedFor.equalsIgnoreCase(RequestFor.PREVIOUS_DATE.name()))
//		   
//	      this.nextday = this.localDate.plus(1L, ChronoUnit.DAYS);
//	      this.preday = this.localDate.minus(1L, ChronoUnit.DAYS);
//	      this.firstDateOfMonth = this.localDate.withDayOfMonth(1);
//	      this.lastDateOfMonth = this.localDate.with(TemporalAdjusters.lastDayOfMonth());
	      
//	      this.todayDate = Date.from(this.localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//	      this.tomorrowDate = Date.from(this.nextday.atStartOfDay(ZoneId.systemDefault()).toInstant());
//	      this.previousDate = Date.from(this.preday.atStartOfDay(ZoneId.systemDefault()).toInstant());
//	      this.firstDateMonth = Date.from(this.firstDateOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
//	      this.lastDateMonth = Date.from(this.lastDateOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
	      
	      if(requestedFor.equalsIgnoreCase(RequestFor.PREVIOUS_DATE.name())) {
	    	  date = Date.from(this.preday.atStartOfDay(ZoneId.systemDefault()).toInstant());
	      } else if(requestedFor.equalsIgnoreCase(RequestFor.NEXT_DATE.name())) {
	    	  date = Date.from(this.nextday.atStartOfDay(ZoneId.systemDefault()).toInstant());
	      }else if(requestedFor.equalsIgnoreCase(RequestFor.MONTH_FIRST_DATE.name())) {
	    	  date = Date.from(this.firstDateOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
	      }else if(requestedFor.equalsIgnoreCase(RequestFor.MONTH_LAST_DATE.name())) {
	    	  date = Date.from(this.lastDateOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
	      }
	      return date;
	   }

}
