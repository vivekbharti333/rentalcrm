import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
// import { UserDetailsRequest } from '../interface/user-management';
import { Constant } from '../core/constant/constants';


@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  public details = false;

  constructor(
    private http: HttpClient,
    private cookieService: CookieService,
  ) {
  }

  // Use getter to always get the latest login user from cookie
  get loginUser() {
    return this.getLoginUser();
  }

  getLoginUser() {
    let details = this.cookieService.get('loginDetails');
    if (details) {
      return JSON.parse(details);
    } else {
      return { 'userId': '', 'fullName': '', 'mobileNo': '', 'memberType': '' };
    }
  }


  logOut() {
    this.cookieService.delete('loginDetails');
  }

  /** Request an OTP to be sent (WhatsApp) for the given registered mobile number. */
  generateOtp(customerMobile: string): Observable<any> {
    const request: any = {
      payload: {
        customerMobile: customerMobile,
      }
    };
    return this.http.post<any>(Constant.Site_Url + 'generateOtp', request, { headers: this.getHeaders() });
  }

  /** Verify the OTP entered by the user for the given mobile number. */
  verifyOtp(customerMobile: string, otp: string): Observable<any> {
    const request: any = {
      payload: {
        customerMobile: customerMobile,
        otp: otp,
      }
    };
    return this.http.post<any>(Constant.Site_Url + 'verifyOtp', request, { headers: this.getHeaders() });
  }

  /** Set a new password for the user identified by loginId (used after OTP verification). */
  changeUserPassword(loginId: string, password: string): Observable<any> {
    const request: any = {
      payload: {
        loginId: loginId,
        password: password,
      }
    };
    return this.http.post<any>(Constant.Site_Url + 'changeUserPassword', request, { headers: this.getHeaders() });
  }

  private getHeaders(): HttpHeaders {
    const token = this.cookieService.get('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

}
