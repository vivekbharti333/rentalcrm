import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { routes } from 'src/app/core/helpers/routes';
import { AuthenticationService } from 'src/app/auth/authentication.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss',
  providers: [MessageService],
})
export class ResetPasswordComponent {
  public routes = routes;
  public password: boolean[] = [false, false, false, false];
  /** Registered mobile number passed from two-step-verification (OTP already verified). */
  mobile: string | null = null;
  /** Login id of the user, returned by generateOtp and forwarded through the flow. */
  loginId: string | null = null;
  newPassword = '';
  confirmPassword = '';
  isLoading = false;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private messageService: MessageService,
  ) {
    const state = this.router.getCurrentNavigation()?.extras?.state as { mobile?: string; loginId?: string } | undefined;
    this.mobile = state?.mobile ?? null;
    this.loginId = state?.loginId ?? null;
  }

  togglePassword(index: number): void {
    this.password[index] = !this.password[index];
  }

  navigation(): void {
    if (!this.mobile) {
      this.messageService.add({
        summary: 'Session expired',
        detail: 'Please start again from Forgot Password.',
        styleClass: 'danger-background-popover',
      });
      this.router.navigate([routes.forgotPassword]);
      return;
    }
    if (!this.loginId) {
      this.messageService.add({
        summary: 'Session expired',
        detail: 'Please start again from Forgot Password.',
        styleClass: 'danger-background-popover',
      });
      this.router.navigate([routes.forgotPassword]);
      return;
    }
    if (!this.newPassword) {
      this.messageService.add({
        summary: 'Required',
        detail: 'Please enter a new password.',
        styleClass: 'danger-background-popover',
      });
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.messageService.add({
        summary: 'Mismatch',
        detail: 'New password and confirm password do not match.',
        styleClass: 'danger-background-popover',
      });
      return;
    }
    if (this.isLoading) return;
    this.isLoading = true;

    this.authenticationService.changeUserPassword(this.loginId, this.newPassword).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        if (response?.responseCode == '200' && response?.payload?.respCode == '200') {
          this.messageService.add({
            summary: response.payload.respCode,
            detail: response.payload.respMesg || 'Password changed successfully.',
            styleClass: 'success-background-popover',
          });
          this.router.navigate([routes.success]);
        } else {
          this.messageService.add({
            summary: String(response?.payload?.respCode || response?.responseCode || 'Failed'),
            detail: response?.payload?.respMesg || response?.responseMessage || 'Unable to change password. Please try again.',
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
}
