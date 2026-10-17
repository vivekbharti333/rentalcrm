import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { routes } from 'src/app/core/helpers/routes';
import { AuthenticationService } from 'src/app/auth/authentication.service';

@Component({
  selector: 'app-two-step-verification',
  templateUrl: './two-step-verification.component.html',
  styleUrl: './two-step-verification.component.scss',
  providers: [MessageService],
})
export class TwoStepVerificationComponent {
  public routes = routes;
  /** Registered mobile number passed from forgot-password. */
  mobile: string | null = null;
  /** Login id of the user (returned by generateOtp), forwarded to reset-password. */
  loginId: string | null = null;
  otpDigits: string[] = ['', '', '', '', '', ''];
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

  onDigitInput(index: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    this.otpDigits[index] = input.value.replace(/\D/g, '').slice(0, 1);
    input.value = this.otpDigits[index];
    // Auto-advance to the next box
    if (this.otpDigits[index] && index < this.otpDigits.length - 1) {
      const next = document.getElementById('digit-' + (index + 2)) as HTMLInputElement | null;
      next?.focus();
    }
  }

  onDigitKeydown(index: number, event: KeyboardEvent): void {
    // Move back on Backspace when the box is empty
    if (event.key === 'Backspace' && !this.otpDigits[index] && index > 0) {
      const prev = document.getElementById('digit-' + index) as HTMLInputElement | null;
      prev?.focus();
    }
  }

  navigation(): void {
    const mobile = (this.mobile || '').trim();
    if (!mobile) {
      this.messageService.add({
        summary: 'Session expired',
        detail: 'Please start again from Forgot Password.',
        styleClass: 'danger-background-popover',
      });
      this.router.navigate([routes.forgotPassword]);
      return;
    }

    const otp = this.otpDigits.join('').trim();
    if (otp.length !== 6) {
      this.messageService.add({
        summary: 'Required',
        detail: 'Please enter the 6 digit OTP sent to your mobile number.',
        styleClass: 'danger-background-popover',
      });
      return;
    }
    if (this.isLoading) return;
    this.isLoading = true;

    this.authenticationService.verifyOtp(mobile, otp).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        if (response?.responseCode == '200' && response?.payload?.respCode == '200') {
          this.messageService.add({
            summary: response.payload.respCode,
            detail: response.payload.respMesg || 'OTP verified successfully.',
            styleClass: 'success-background-popover',
          });
          this.router.navigate([routes.resetPassword], {
            state: { mobile: mobile, loginId: this.loginId || '' },
          });
        } else {
          this.messageService.add({
            summary: String(response?.payload?.respCode || response?.responseCode || 'Failed'),
            detail: response?.payload?.respMesg || response?.responseMessage || 'OTP verification failed. Please try again.',
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
