package com.minhpt.smart_wallet_service.i18n;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MessageResolver {

    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("vi");
    public static final Locale ENGLISH_LOCALE = Locale.ENGLISH;
    public static final List<Locale> SUPPORTED_LOCALES = List.of(DEFAULT_LOCALE, ENGLISH_LOCALE);

    private static final Map<String, String> DIRECT_MESSAGE_CODES = Map.ofEntries(
            Map.entry("Thành công", "response.success"),
            Map.entry("User created successfully", "response.user.created"),
            Map.entry("Update successfully", "response.user.updated"),
            Map.entry("Validation falied", "response.validation_failed"),
            Map.entry("Forbidden", "security.forbidden"),
            Map.entry("Unauthorized", "security.unauthorized"),
            Map.entry("Invalid or expired token", "security.token.invalid_or_expired"),
            Map.entry("User not found or inactive", "security.user.not_found_or_inactive"),
            Map.entry("Username is required", "validation.username.required"),
            Map.entry("Username must be required", "validation.username.required"),
            Map.entry("Email is required", "validation.email.required"),
            Map.entry("Email invalid", "validation.email.invalid"),
            Map.entry("Password is required", "validation.password.required"),
            Map.entry("Password must be required", "validation.password.required"),
            Map.entry("Amount is required", "validation.amount.required"),
            Map.entry("Amount must be greater than 0", "validation.amount.positive"),
            Map.entry("Type is required", "validation.type.required"),
            Map.entry("Type must be either INCOME or EXPENSE", "validation.transaction.type.invalid"),
            Map.entry("Description is required", "validation.description.required"),
            Map.entry("Category is required", "validation.category.required"),
            Map.entry("Transaction date is required", "validation.transaction_date.required"),
            Map.entry("Transantion date cannot be in the future", "validation.transaction_date.past_or_present"),
            Map.entry("Full name must not be blank", "validation.full_name.required"),
            Map.entry("Invalid phone number", "validation.phone.invalid"),
            Map.entry("Saving goal is required", "validation.saving_goal.required"),
            Map.entry("Start time is required", "validation.start_time.required"),
            Map.entry("Start time must be in the present or future", "validation.start_time.future_or_present"),
            Map.entry("Recurrence type is required", "validation.recurrence_type.required"),
            Map.entry("Recurrence type must be DAILY, WEEKLY or MONTHLY", "validation.recurrence_type.invalid"),
            Map.entry("Interval value must be greater than or equal to 1", "validation.interval.min"),
            Map.entry("Name is required", "validation.name.required"),
            Map.entry("Icon is required", "validation.icon.required"),
            Map.entry("Bạn phải lựa chọn gói thanh toán", "validation.subscription_plan.required"),
            Map.entry("Bạn phải chọn phương thức thanh toán (Momo hoặc Vnpay)", "validation.payment_provider.required"),
            Map.entry("Target Amount is required", "validation.target_amount.required"),
            Map.entry("Target Amount must be > 0", "validation.target_amount.positive"),
            Map.entry("currentAmount Amount is required", "validation.current_amount.required"),
            Map.entry("currentAmount Amount must be >= 0", "validation.current_amount.non_negative"),
            Map.entry("Deadline is required", "validation.deadline.required"),
            Map.entry("Deadline is in the present or future", "validation.deadline.future_or_present"),
            Map.entry("Invalid credentials", "error.auth.invalid_credentials"),
            Map.entry("User chưa đăng nhập", "error.user.not_authenticated"),
            Map.entry("Cannot process OCR file", "error.ocr.process_failed"),
            Map.entry("File is empty", "error.file.empty"),
            Map.entry("Only image files are supported", "error.file.image_only"),
            Map.entry("Invalid token", "error.token.invalid"),
            Map.entry("Token already used", "error.token.used"),
            Map.entry("Token expired", "error.token.expired"),
            Map.entry("Invalid refresh token", "error.refresh_token.invalid"),
            Map.entry("Expired refresh token", "error.refresh_token.expired"),
            Map.entry("At least one role is required", "error.role.at_least_one_required"),
            Map.entry("Role name must not be blank", "error.role.name.blank"),
            Map.entry("Register request must not be null", "error.user.register_request_null"),
            Map.entry("Username may only contain letters, numbers, dot, underscore, or hyphen", "error.user.username.pattern"),
            Map.entry("Password must not be blank", "error.user.password.blank"),
            Map.entry("Password must not contain spaces", "error.user.password.no_spaces"),
            Map.entry("Password must contain uppercase, lowercase, number, and special character", "error.user.password.complexity"),
            Map.entry("Username already exists", "error.user.username.exists"),
            Map.entry("Email already exists", "error.user.email.exists"),
            Map.entry("Username must not be blank", "error.user.username.blank"),
            Map.entry("Email must not be blank", "error.user.email.blank"),
            Map.entry("Phone number must not be blank", "error.user.phone.blank"),
            Map.entry("Update request must not be null", "error.user.update_request_null"),
            Map.entry("At least one field must be provided", "error.user.at_least_one_field"),
            Map.entry("Month and year are required", "error.wallet_summary.month_year_required"),
            Map.entry("Transaction amount is invalid", "error.transaction.amount_invalid"),
            Map.entry("Deposit amount exceeds saving goal target", "error.saving_goal.deposit_exceeds_target"),
            Map.entry("User is not authenticated", "error.user.not_authenticated"),
            Map.entry("Insufficient wallet balance", "error.account_balance.insufficient"),
            Map.entry("Balance must not be null", "error.account_balance.null"),
            Map.entry("Balance must not be negative", "error.account_balance.negative"),
            Map.entry("Only pending schedules can be canceled", "error.schedule.only_pending_cancel"),
            Map.entry("End time must be greater than or equal to start time", "error.schedule.end_before_start"),
            Map.entry("Day of month must be between 1 and 31", "error.schedule.day_of_month.invalid"),
            Map.entry("No schedule occurrence is available before end time", "error.schedule.no_occurrence_before_end"),
            Map.entry("At least one day of week is required for weekly schedules", "error.schedule.days_of_week_required"),
            Map.entry("Unknown error", "error.schedule.unknown"),
            Map.entry("Không thể cập nhật hạng mục do được tạo bởi hệ thống", "error.category.system_update_not_allowed"),
            Map.entry("Không thể cập nhật hạng mục do không phải người tạo", "error.category.owner_update_not_allowed"),
            Map.entry("Không thể xóa hạng mục do được tạo bởi hệ thống", "error.category.system_delete_not_allowed"),
            Map.entry("Không thể xóa hạng mục do không phải người tạo", "error.category.owner_delete_not_allowed"),
            Map.entry("Gửi email verify thất bại", "error.email.verify_send_failed"),
            Map.entry("Không tìm thấy category để AI mapping giao dịch", "error.openai.categories_missing"),
            Map.entry("Không parse được phản hồi từ OpenAI", "error.openai.parse_failed"),
            Map.entry("OpenAI request bị gián đoạn", "error.openai.interrupted"),
            Map.entry("OpenAI không trả về nội dung transaction hợp lệ", "error.openai.invalid_transaction_content"),
            Map.entry("OpenAI không trả về transaction hợp lệ", "error.openai.invalid_transaction_content"),
            Map.entry("OpenAI API key chưa được cấu hình", "error.openai.api_key_missing"),
            Map.entry("OpenAI không trả về danh sách transaction", "error.openai.transactions_missing"),
            Map.entry("OpenAI trả về categoryId không tồn tại trong danh sách category", "error.openai.category_id_invalid"),
            Map.entry("OpenAI trả về description rỗng", "error.openai.description_blank"),
            Map.entry("OpenAI trả về amount không hợp lệ", "error.openai.amount_invalid"),
            Map.entry("OpenAI trả về type không hợp lệ", "error.openai.type_invalid"),
            Map.entry("OpenAI trả về transactionDate không đúng định dạng dd/MM/yyyy", "error.openai.transaction_date_invalid")
    );

    private static final List<PatternRule> PATTERN_MESSAGE_CODES = List.of(
            rule("^Không đọc được file template: (.+)$", "error.template.read_failed", 1),
            rule("^Subscription plan not found by id = (.+)$", "error.payment.subscription_plan.not_found", 1),
            rule("^Không tìm thấy giao dịch nào tương ứng với id = (.+)$", "error.payment.transaction.not_found", 1),
            rule("^Transaction not found for orderCode: (.+)$", "error.payment.transaction.not_found_by_order", 1),
            rule("^Chưa hoàn tất giao dịch với mã order thanh toán: (.+)$", "error.payment.transaction.not_completed", 1),
            rule("^Category not found by ID = (.+)$", "error.category.not_found", 1),
            rule("^Không tìm thấy hạng mục tưng ứng id = (.+)$", "error.category.not_found", 1),
            rule("^Saving goal not found by ID = (.+)$", "error.saving_goal.not_found", 1),
            rule("^Transaction not found by ID = (.+)$", "error.transaction.not_found", 1),
            rule("^User not found by id = (.+)$", "error.user.not_found", 1),
            rule("^User not found by ID = (.+)$", "error.user.not_found", 1),
            rule("^Unsupported payment method: (.+)$", "error.payment.method.unsupported", 1),
            rule("^Unsupported provider: (.+)$", "error.payment.provider.unsupported", 1),
            rule("^Invalid transaction type: (.+)$", "error.transaction.type_invalid", 1),
            rule("^Roles not found: (.+)$", "error.role.not_found", 1),
            rule("^Username length must be between (\\d+) and (\\d+) characters$", "error.user.username.length", 1, 2),
            rule("^Email length must not exceed (\\d+) characters$", "error.user.email.length", 1),
            rule("^Password length must be between (\\d+) and (\\d+) characters$", "error.user.password.length", 1, 2),
            rule("^Full name must not exceed (\\d+) characters$", "validation.full_name.max", 1),
            rule("^OCR failed: (.+)$", "error.ocr.failed", 1),
            rule("^Unsupported schedule action: (.+)$", "error.schedule.action_unsupported", 1),
            rule("^Unsupported recurrence type: (.+)$", "error.schedule.recurrence_unsupported", 1),
            rule("^Invalid day of week: (.+)$", "error.schedule.invalid_day_of_week", 1),
            rule("^OpenAI request failed \\((\\d+)\\): (.+)$", "error.openai.request_failed", 1, 2),
            rule("^OpenAI request failed with status (\\d+)$", "error.openai.request_failed_status", 1)
    );

    private final MessageSource messageSource;

    public MessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Locale getCurrentLocale() {
        return normalizeLocale(LocaleContextHolder.getLocale());
    }

    public Locale resolveLocale(@Nullable HttpServletRequest request) {
        if (request == null) {
            return DEFAULT_LOCALE;
        }
        return normalizeLocale(request.getLocale());
    }

    public Locale normalizeLocale(@Nullable Locale locale) {
        if (locale != null) {
            String language = locale.getLanguage();
            if ("en".equalsIgnoreCase(language)) {
                return ENGLISH_LOCALE;
            }
            if ("vi".equalsIgnoreCase(language)) {
                return DEFAULT_LOCALE;
            }
        }
        return DEFAULT_LOCALE;
    }

    public String get(String code, Object... args) {
        return get(getCurrentLocale(), code, args);
    }

    public String get(@Nullable Locale locale, String code, Object... args) {
        String resolvedMessage = messageSource.getMessage(code, args, null, normalizeLocale(locale));
        return resolvedMessage != null ? resolvedMessage : code;
    }

    public String resolve(@Nullable String messageOrCode) {
        return resolve(getCurrentLocale(), messageOrCode);
    }

    public String resolve(@Nullable Locale locale, @Nullable String messageOrCode) {
        if (messageOrCode == null || messageOrCode.isBlank()) {
            return messageOrCode;
        }

        Locale resolvedLocale = normalizeLocale(locale);
        String translatedCode = messageSource.getMessage(messageOrCode, null, null, resolvedLocale);
        if (translatedCode != null) {
            return translatedCode;
        }

        String aliasCode = DIRECT_MESSAGE_CODES.get(messageOrCode);
        if (aliasCode != null) {
            return get(resolvedLocale, aliasCode);
        }

        for (PatternRule rule : PATTERN_MESSAGE_CODES) {
            String translatedMessage = rule.resolve(this, resolvedLocale, messageOrCode);
            if (translatedMessage != null) {
                return translatedMessage;
            }
        }

        return messageOrCode;
    }

    private static PatternRule rule(String pattern, String code, int... groupIndexes) {
        return new PatternRule(Pattern.compile(pattern), code, groupIndexes);
    }

    private record PatternRule(Pattern pattern, String code, int[] groupIndexes) {
        private String resolve(MessageResolver messageResolver, Locale locale, String rawMessage) {
            Matcher matcher = pattern.matcher(rawMessage);
            if (!matcher.matches()) {
                return null;
            }

            Object[] arguments = new Object[groupIndexes.length];
            for (int i = 0; i < groupIndexes.length; i++) {
                arguments[i] = matcher.group(groupIndexes[i]);
            }

            return messageResolver.get(locale, code, arguments);
        }
    }
}
