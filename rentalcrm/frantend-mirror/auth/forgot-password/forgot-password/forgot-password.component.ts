import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { routes } from 'src/app/core/helpers/routes';
import { AuthenticationService } from 'src/app/auth/authentication.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
  providers: [MessageService],
})
export class ForgotPasswordComponent {
  public routes = routes;
  customerMobile = '';
  isLoading = false;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private messageService: MessageService,
  ) {}

  onSubmit(): void {
    const mobile = (this.customerMobile || '').trim();
    if (!mobile) {
      this.messageService.add({
        summary: 'Required',
        detail: 'Please enter your registered mobile number.',
        styleClass: 'danger-background-popover',
      });
      return;
    }
    if (this.isLoading) return;
    this.isLoading = true;

    this.authenticationService.generateOtp(mobile).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        if (response?.responseCode == '200' && response?.payload?.respCode == '200') {
          this.messageService.add({
            summary: response.payload.respCode,
            detail: response.payload.respMesg || 'OTP sent successfully.',
            styleClass: 'success-background-popover',
          });
          this.router.navigate([routes.twoStepVerification], {
            state: { mobile: mobile, loginId: response.payload.loginId || '' },
          });
        } else {
          this.messageService.add({
            summary: String(response?.payload?.respCode || response?.responseCode || 'Failed'),
            detail: response?.payload?.respMesg || response?.responseMessage || 'Unable to send OTP. Please try again.',
            styleClass: 'danger-background-popover',
          });
        }
      },
      error: (error: any) => {
        this.isLoading = false;
        this.messageService.add({
          summary: error.status ? String(error.status) : 'Connection error',
          detail: error.error?.responseMessage || (error.status === 0
            ? 'Unable to reach the server. Check the connection and server availability.'
            : 'The server returned an error. Please try again.'),
          styleClass: 'danger-background-popover',
        });
      },
    });
  }

  backToSignIn(): void {
    this.router.navigate([routes.signIn]);
  }
}
