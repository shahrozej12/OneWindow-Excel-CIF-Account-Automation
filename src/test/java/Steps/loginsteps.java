package Steps;

import define.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import locators.XPathLocators;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;

    public class loginsteps {

        public static WebDriver driver;
        private static WebDriverWait wait;
        private static CIF_actions cifHelpers;
        private static Account_actions accHelpers;
        private DatePickerHandler date;
        private AutoITHandler autoITHandler;
        private static String sectorCode;
        private static String nationality;
        private static Checker_Tracking_CIF trackingCif;
        private static RequestApprovalSteps requestApprovalSteps;
        private ExcelHelper excelHelper;
        private static Map<String, String> currentRowData;
        private static final String LOGIN_URL = "http://10.111.202.145:9060/login";


        public loginsteps() {

        }


        @Given("I am on the login page")
        public static void i_am_on_the_login_page() {
            // Initialize Chrome options (you can add options if necessary)
            ChromeOptions options = new ChromeOptions();
            options.setBinary("C:\\Users\\Shahroze.Janjua\\Downloads\\chrome-win64_stable\\chrome-win64\\chrome.exe");

            driver = new ChromeDriver(options);

            // Initialize WebDriverWait
            wait = new WebDriverWait(driver, Duration.ofSeconds(45));
            driver.get(LOGIN_URL);
            driver.manage().window().maximize();
            wait = new WebDriverWait(driver, Duration.ofSeconds(45));
            cifHelpers = new CIF_actions(driver);
            accHelpers = new Account_actions(driver);
            trackingCif = new Checker_Tracking_CIF(driver);
            requestApprovalSteps = new RequestApprovalSteps(driver);

        }

    @And("I fill in the login information {string} and {string}")
    public static void iFillInTheLoginInformationAnd(String username, String password) {
        driver.findElement(By.xpath(XPathLocators.USERNAME_FIELD)).sendKeys(username);
        driver.findElement(By.xpath(XPathLocators.PASSWORD_FIELD)).sendKeys(password);
        pause(2000);
    }


    @When("I log in")
    public static void i_log_in() {
        driver.findElement(By.xpath(XPathLocators.LOGIN_BUTTON)).click();
        checkForToast(); // Check for toast after this step
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Customer_UAT)));
    }

    @When("I navigate to customer information")
    public static void i_navigate_to_customer_information() throws InterruptedException {
        WebElement customerInfoButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Customer_UAT)));
        customerInfoButton.click();
        pause(2000);
        waitUntilSpinnerIsInvisible();
        runThreadsAfterFormSubmission();

    }

    @When("I navigate to CIF")
    public static void i_navigate_to_cif() {
        WebElement cifButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.CIF_OPENING_BUTTON)));
        cifButton.click();
        waitUntilSpinnerIsInvisible();


    }
    private static void setDateUsingJavaScript(String xpath, String dateValue) {
        WebElement dateElement = driver.findElement(By.xpath(xpath));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", dateElement, dateValue);
    }

    @When("I fill in the customer information:")
    public static void i_fill_in_the_customer_information() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Map<String, String> data = currentRowData;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.CUSTOMER_CATEGORY_DROPDOWN)));

        if (data != null) {
            cifHelpers.selectDropdownOption(XPathLocators.CUSTOMER_CATEGORY_DROPDOWN, data.get("Customer Category"));
            pause(1000);
            cifHelpers.selectIdDocumentType(XPathLocators.ID_DOCUMENT_TYPE_DROPDOWN, data.get("ID Document Type"));
            pause(1000);
            String idDocNo = data.get("ID Document Number");
            cifHelpers.enterCnicAndClickButton(idDocNo); // Pass CNIC here
            // Get date field elements
            String cnicIssuanceDate = data.get("CNIC Issuance Date");
            String cnicExpiryDate = data.get("CNIC Expiry Date");
            String visaExpiryDate = data.get("VISA Expiry Date");
            String dob = data.get("Date of Birth");

////            // Initialize DatePickerHandler
//            DatePickerHandler date = new DatePickerHandler(driver);
//
//            // Handle CNIC Issuance Date
//            date.openDatePickerAndSelectDate(XPathLocators.CNIC_ISSUANCE_DATE_FIELD, cnicIssuanceDate, "", "issuance");
//
//           // Handle CNIC Expiry Date (ensure it is after Issuance Date)
//            date.openDatePickerAndSelectDate(XPathLocators.CNIC_EXPIRY_DATE_FIELD, cnicExpiryDate, "", "expiry");
//
//            // Handle Visa Expiry Date (ensure it is after today)
//            date.openDatePickerAndSelectDate(XPathLocators.VISA_EXPIRY_DATE, visaExpiryDate, "", "visa");
//
//            // Handle DOB (ensure it is 18 years ago)
//            date.openDatePickerAndSelectDate(XPathLocators.DOB, dob, "", "dob");

        // Use JavaScript to set CNIC Issuance Date
            setDateUsingJavaScript(XPathLocators.CNIC_ISSUANCE_DATE_FIELD, cnicIssuanceDate);

            // Use JavaScript to set CNIC Expiry Date
            setDateUsingJavaScript(XPathLocators.CNIC_EXPIRY_DATE_FIELD, cnicExpiryDate);

            // Use JavaScript to set Visa Expiry Date
            setDateUsingJavaScript(XPathLocators.VISA_EXPIRY_DATE, visaExpiryDate);

            // Use JavaScript to set DOB
            setDateUsingJavaScript(XPathLocators.DOB, dob);

            cifHelpers.selectCustomerSegment(XPathLocators.SECTOR_CODE_DROPDOWN, data.get("Sector Code"));
            pause(1000);
            cifHelpers.selectNationality(XPathLocators.NATIONALITY_DROPDOWN, data.get("Nationality"));
            pause(1000);
            cifHelpers.selectAssanAccount(XPathLocators.ASAN_ACCOUNT_DROPDOWN, data.get("Assan Account"));
            pause(1000);
            cifHelpers.selectMarital(XPathLocators.MARITAL_STATUS_DROPDOWN, data.get("Marital Status"));
            pause(1000);

            // Customer Title Validation
            String customerTitle = data.get("Customer Title");
            if (customerTitle != null && !customerTitle.isEmpty()) {
                // Select the Customer Title dropdown value
                cifHelpers.selectCustomerTitle(XPathLocators.CUSTOMER_TITLE_DROPDOWN, customerTitle);
            } else {
                System.out.println("Customer Title is missing or empty.");
                throw new IllegalArgumentException("Customer Title is missing or empty.");
            }
            pause(1000);

            // Get Gender value
            String gender = data.get("Gender");
            if (gender != null && !gender.isEmpty()) {
                // Validate Gender based on Customer Title
                if ("Mr.".equalsIgnoreCase(customerTitle)) {
                    if (!"Male".equalsIgnoreCase(gender)) {
                        throw new IllegalArgumentException("For 'Mr.' title, gender must be Male. Provided: " + gender);
                    }
                } else if ("Dr.".equalsIgnoreCase(customerTitle)) {
                    // 'Dr.' can be Male or Female
                    if (!"Male".equalsIgnoreCase(gender) && !"Female".equalsIgnoreCase(gender)) {
                        throw new IllegalArgumentException("For 'Dr.' title, gender must be either Male or Female. Provided: " + gender);
                    }
                } else if ("Ms.".equalsIgnoreCase(customerTitle) || "Mrs.".equalsIgnoreCase(customerTitle) || "M/S.".equalsIgnoreCase(customerTitle)) {
                    if (!"Female".equalsIgnoreCase(gender)) {
                        throw new IllegalArgumentException("For 'Ms.', 'Mrs.', or 'M/S.' titles, gender must be Female. Provided: " + gender);
                    }
                } else {
                    // If title is not recognized
                    throw new IllegalArgumentException("Gender validation failed for title: " + customerTitle);
                }

                // If the gender is valid, select the gender from the dropdown
                cifHelpers.selectGender(XPathLocators.GENDER_DROPDOWN, gender);
            } else {
                System.out.println("Gender is missing or empty.");
                throw new IllegalArgumentException("Gender is missing or empty.");
            }


            // Fill in personal information
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.FIRST_NAME_FIELD))).sendKeys(data.get("First Name"));
            pause(1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.MIDDLE_NAME_FIELD))).sendKeys(data.get("Middle Name"));
            pause(1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.LAST_NAME_FIELD))).sendKeys(data.get("Last Name"));
            pause(1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.FATHER_NAME_FIELD))).sendKeys(data.get("Father's Name"));
            pause(1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.MOTHER_NAME_FIELD))).sendKeys(data.get("Mother's Name"));
            pause(1000);


            // Check if nationality is NOT Pakistan
            nationality = data.get("Nationality");

            if (!data.get("Nationality").equalsIgnoreCase("Pakistan")) {
                // If nationality is not Pakistan,
                cifHelpers.selectTaxReason(XPathLocators.TAX_REASON_DROPDOWN, data.get("Tax Reason (CRS)"));
                pause(1000);
                cifHelpers.selectTaxResidence(XPathLocators.TAX_RESIDENCY_DROPDOWN, data.get("Tax Residence (CRS)"));
                pause(1000);

            } else {

            }


            // Check if Sector Code is "Sole Proprietorship" or "Self-Employed" to decide whether to fill in Occupation and Industry Code
            sectorCode = data.get("Sector Code");
            if ("1100 - Sole Proprietership".equalsIgnoreCase(sectorCode) || "1019 - Self Employed".equalsIgnoreCase(sectorCode)) {
                cifHelpers.selectOccupation(XPathLocators.OCCUPATION_DROPDOWN, data.get("Occupation"));
                cifHelpers.selectIndusCode(XPathLocators.INDUSTRY_CODE_DROPDOWN, data.get("Industry Code"));
            }
        }
    }

    @Then("Click on CUST_INFO_Next")
    public static void clickOnCUST_INFO_Next() {
        WebElement customerInfoNextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.CUSTOMER_INFORMATION_NEXT)));
        customerInfoNextButton.click();
    }


    @When("I fill in the PEP-Customer Demographic:")
    public static void iFillInThePEPCustomerDemographic() {
        Map<String, String> data = currentRowData;


        cifHelpers.selectPep(XPathLocators.PEP_DROPDOWN, data.get("PEP"));
        pause(1000);
        // Check if the PEP value is not "No", then fill in the PEP Status dropdown
        if (!data.get("PEP").equalsIgnoreCase("No")) {
            cifHelpers.selectPepStatus(XPathLocators.PEP_STATUS_DROPDOWN, data.get("PEP Status"));
            pause(1000);
        } else {
            // Optionally, you can disable the PEP Status field or log a message
            System.out.println("PEP Status dropdown is disabled because PEP is selected as 'No'");
        }
        cifHelpers.selectAddressType(XPathLocators.ADDRESS_TYPE, data.get("Address Type"));
        pause(1000);
        cifHelpers.selectProvince(XPathLocators.PROVINCE, data.get("Province"));
        pause(1000);
        cifHelpers.selectEmployee_Status(XPathLocators.EMP_STATUS, data.get("Employee Status"));
        pause(1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.EMP_NAME))).sendKeys(data.get("Employer Name"));
        pause(1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.DESIGNATION))).sendKeys(data.get("Designation"));
        pause(1000);
        cifHelpers.selectRMCode(XPathLocators.RM_CODE, data.get("RM Code"));
        pause(1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.DSRCODE))).sendKeys(data.get("DSR Code"));
        pause(1000);
        cifHelpers.selectEducation(XPathLocators.EDUCATION, data.get("Education"));
        pause(1000);
        cifHelpers.selectPlaceBirth(XPathLocators.PLACE_BIRTH, data.get("Place of Birth"));
        pause(1000);

        if ("1100 - Sole Proprietership".equalsIgnoreCase(sectorCode)) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.BUSINESS_NAME)))
                    .sendKeys(data.get("Business Name"));
            pause(1000);

        } else {
            // Do nothing in the else case (you can leave it empty or log an info message)
        }

    }

    @Then("Click on PEP_Next")
    public static void clickOnPEP_Next() {
        WebElement customerInfoNextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.PEP_NEXT)));
        customerInfoNextButton.click();
    }

    @When("I fill in the Contact Detail:")
    public static void iFillInTheContactDetail() {
        Map<String, String> data = currentRowData;


        // Cell Mobile Number Validation
        String cellCountryCode = data.get("Cell Country Code");
        String cellMobileNumber = data.get("Cell Mobile Number");
        if ("Pakistan".equalsIgnoreCase(cellCountryCode)) {
            // Ensure the cell mobile number starts with "03" and is exactly 11 digits long
            if (cellMobileNumber.startsWith("03") && cellMobileNumber.length() == 11 && cellMobileNumber.matches("\\d{11}")) {
                // Valid mobile number
                cifHelpers.selectCellCountryCode(XPathLocators.Cell_Country_Code, cellCountryCode);
                pause(1000);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Cell_No)))
                        .sendKeys(cellMobileNumber);
                pause(1000);
            } else if (cellMobileNumber.length() == 10 && cellMobileNumber.matches("\\d{10}")) {
                // If the number is 10 digits long (missing '03' prefix), add '03' at the beginning
                String correctedMobileNumber = "03" + cellMobileNumber;
                cifHelpers.selectCellCountryCode(XPathLocators.Cell_Country_Code, cellCountryCode);
                pause(1000);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Cell_No)))
                        .sendKeys(correctedMobileNumber);
                pause(1000);
            } else {
                throw new IllegalArgumentException("Invalid Cell Mobile Number for Pakistan: Must start with '03' and be exactly 11 digits." + cellMobileNumber);
            }
        } else {
            // If not Pakistan, just ensure the number is exactly 11 digits
            if (cellMobileNumber.length() == 11 && cellMobileNumber.matches("\\d{11}")) {
                cifHelpers.selectCellCountryCode(XPathLocators.Cell_Country_Code, cellCountryCode);
                pause(1000);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Cell_No)))
                        .sendKeys(cellMobileNumber);
                pause(1000);
            } else {
                throw new IllegalArgumentException("Invalid Cell Mobile Number: Must be exactly 11 digits:" + cellMobileNumber);
            }
        }


        // Residence Phone Number Validation
        String resCountryCode = data.get("Residence Country Code");
        String resPhoneNumber = data.get("Residence Phone Number");
        if ("Pakistan".equalsIgnoreCase(resCountryCode)) {
            // Ensure residence phone number starts with "03" and is exactly 11 digits long
            if (resPhoneNumber.startsWith("03") && resPhoneNumber.length() == 11 && resPhoneNumber.matches("\\d{11}")) {
                cifHelpers.selectResCounryCode(XPathLocators.Res_Country_Code, resCountryCode);
                pause(1000);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Res_No)))
                        .sendKeys(resPhoneNumber);
                pause(1000);
            } else {
                throw new IllegalArgumentException("Invalid Residence Phone Number for Pakistan: Must start with '03' and be exactly 11 digits.");
            }
        } else {
            // If not Pakistan, just ensure the number is exactly 11 digits
            if (resPhoneNumber.length() == 11 && resPhoneNumber.matches("\\d{11}")) {
                cifHelpers.selectResCounryCode(XPathLocators.Res_Country_Code, resCountryCode);
                pause(1000);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Res_No)))
                        .sendKeys(resPhoneNumber);
                pause(1000);
            } else {
                throw new IllegalArgumentException("Invalid Residence Phone Number: Must be exactly 11 digits.");
            }
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Res_Add_1)))
                .sendKeys(data.get("Residential Address Line 1"));
        pause(1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Res_Add_2)))
                .sendKeys(data.get("Residential Address Line 2"));
        pause(1000);

        // Email Address Validation: Must contain "@" and end with ".com"
        String emailAddress = data.get("Email Address");
        if (emailAddress.contains("@") && emailAddress.endsWith(".com")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Email)))
                    .sendKeys(emailAddress);
            pause(1000);
        } else {
            System.out.println("Invalid Email Address: Must contain '@' and end with '.com'.");
            throw new IllegalArgumentException("Invalid Email Address: Must contain '@' and end with '.com'.");
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Office_Add_1)))
                .sendKeys(data.get("Office / Business Address Line 1"));
        pause(1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Office_Add_2)))
                .sendKeys(data.get("Office / Business Address Line 2"));
        pause(1000);
        cifHelpers.selectMailingAdd(XPathLocators.Pref_Mail_Add, data.get("Preferred Mailing Address"));
        pause(1000);
        //        cifHelpers.selectMailingAdd(XPathLocators.Mobile_Telecom_Contact, data.get("Mobile Telecom"));
        //        pause(1000);
    }

    @When("I fill in the Next Of Kin Detail:")
    public static void iFillInTheNextOfKinDetail() {
        Map<String, String> data = currentRowData;

        pause(1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Kin_Name)))
                .sendKeys(data.get("Next of Kin Name"));
        pause(1000);
        cifHelpers. selectKinRelationship(XPathLocators.Kin_relationship, data.get("Next of Kin Relationship"));
        pause(1000);
        String mobileNumber = data.get("Next of Kin Mobile Number");

        if (mobileNumber.length() == 11 && mobileNumber.matches("\\d{11}")) {
            // Proceed with entering the valid mobile number
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Kin_No)))
                    .sendKeys(mobileNumber);
            pause(1000); // Optional: Pause after entering the value
        } else if (mobileNumber.length() == 10 && mobileNumber.matches("\\d{10}") && mobileNumber.startsWith("3")) {
            // Add '0' at the beginning if it's 10 digits long and starts with '3'
            mobileNumber = "0" + mobileNumber;
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Kin_No)))
                    .sendKeys(mobileNumber);
            pause(1000); // Optional: Pause after entering the value
        } else {
            System.out.println("Invalid Mobile number");
            throw new IllegalArgumentException("Invalid mobile number. The number must be 11 digits long and start with '03'.");
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Kin_Add_1)))
                .sendKeys(data.get("Next of Kin Address Line 1"));
        pause(1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Kin_Add_2)))
                .sendKeys(data.get("Next of Kin Address Line 2"));
        pause(1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.KYC_Remarks)))
                .sendKeys(data.get("KYC Remarks"));
        pause(1000);
    }

    // Method to pause execution for a specified time in milliseconds
    private static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }


    @Then("Click on CONT_DET_Next")
    public static void clickOnCONT_DET_Next() {
        WebElement contactDetailNextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Cont_det_Next)));
        contactDetailNextButton.click();
    }


    @Then("Click on KIN_Next")
    public static void clickOnKIN_Next() {
        WebElement contactDetailNextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Proc_To_Acc)));
        contactDetailNextButton.click();
    }

    @When("I fill in the Account Detail:")
    public static void iFillInTheAccountDetail() {
        Map<String, String> data = currentRowData;


        // Fill dropdown fields based on the Gherkin table data
        accHelpers.selectAccOpType(XPathLocators.Acc_Op_Type, data.get("Account Operational Type"));
        pause(1000);


        accHelpers.selectAccType(XPathLocators.Acc_Type, data.get("Account Type"));
        pause(1000);

        accHelpers.selectCurrency(XPathLocators.Currency, data.get("Currency"));
        pause(1000);

        // Select Debit Card Request only if NOT "JOINTLY"
        if (!"JOINTLY".equalsIgnoreCase(data.get("Account Operational Type"))) {
            accHelpers.selectDebitRequest(XPathLocators.Debit_Request, data.get("Debit Card Request"));
            pause(1000);

            // Handle Debit Card logic
            if (data.get("Debit Card Request").equalsIgnoreCase("YES")) {
                accHelpers.selectDebitCardType(XPathLocators.Debit_Card_Type, data.get("Debit Card Type"));
                pause(1000);

                accHelpers.selectDebitCardPickupBranch(XPathLocators.Debit_Card_Pickup_branch, data.get("Debit Card Pickup Branch"));
                pause(1000);
            } else {
                System.out.println("No need of Debit Card");
            }
        } else {
            System.out.println("Debit Card Request is disabled for JOINTLY accounts.");

        }

        accHelpers.selectChequeBook(XPathLocators.Cheque_Book, data.get("Cheque Book Request"));
        pause(1000);
        // Handle Credit Card logic
        if (data.get("Cheque Book Request").equalsIgnoreCase("YES")) {
            accHelpers.selectChequeBookLeaves(XPathLocators.Cheque_Book_Leaves, data.get("Cheque Book Leaves"));
            pause(1000);

            accHelpers.selectChequeBookPickupBranch(XPathLocators.Cheque_Book_Pickup_Branch, data.get("Cheque Book Pickup Branch"));
            pause(1000);
        } else {
            System.out.println("No need of Debit Card");

        }


        accHelpers.selectGeoBusSpread(XPathLocators.Geo_Bus_Spread, data.get("Geo Business Spread"));
        pause(1000);

        accHelpers.selectTypeTrans(XPathLocators.Type_Trans, data.get("Type of Transactions"));
        pause(1000);

        accHelpers.selectExpModesTrans(XPathLocators.Exp_modes_Trans, data.get("Expected Modes of Transaction"));
        pause(1000);

        accHelpers.selectExpCountParties(XPathLocators.Exp_Count_Parties, data.get("Expected Counter Parties"));
        pause(1000);


        // Retrieve the value from Excel
        String debitValue = data.get("Expected Monthly Debit Transactions");
        String creditValue = data.get("Expected Monthly Credit Transactions");

        // Remove the decimal part if the value is a whole number (ends with .0)
        debitValue = debitValue.matches("\\d+\\.0$") ? debitValue.substring(0, debitValue.indexOf(".0")) : debitValue;
        creditValue = creditValue.matches("\\d+\\.0$") ? creditValue.substring(0, creditValue.indexOf(".0")) : creditValue;

        // Pass the cleaned values to the methods
        accHelpers.selectExpMonDebTrans(XPathLocators.Exp_Mon_Deb_Trans, debitValue);
        pause(1000);

        accHelpers.selectExpMonCredTrans(XPathLocators.Exp_Mon_Cred_Trans, creditValue);
        pause(1000);


        //        accHelpers.selectExpMonDebTurn(XPathLocators.Exp_Mon_Deb_Turn, data.get("Expected Monthly Debit Turnover"));
        //        pause(1000);
        //
        //        accHelpers.selectExpMonCredTurn(XPathLocators.Exp_Mon_Cred_Turn, data.get("Expected Monthly Credit Turnover"));
        //        pause(1000);

        accHelpers.selectProvince(XPathLocators.Province, data.get("Provinces"));
        pause(1000);

        accHelpers.selectPhotoAcc(XPathLocators.Photo_Acc, data.get("Photo Account"));
        pause(1000);
        accHelpers.selectJSAccHolder(XPathLocators.JS_Acc_Holder, data.get("JS Account Holder"));
        pause(1000);
        if (data.get("JS Account Holder").equalsIgnoreCase("YES")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.JS_Acc)))
                    .sendKeys(data.get("JS Account Number"));
            pause(1000);
        } else {
            // Optionally, you can disable the PEP Status field or log a message
            accHelpers.selectCountParty(XPathLocators.Counter_Party_Industry, data.get("Counter Party Industry"));
            pause(1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.Counter_Party_Name)))
                    .sendKeys(data.get("Name of Counter Party"));
            pause(1000);
        }

        pause(1000);
        // Handle E-Statement logic
        accHelpers.selectEStatement(XPathLocators.E_Statement, data.get("E-Statement"));
        pause(1000);

        if (data.get("E-Statement").equalsIgnoreCase("YES")) {
            // Enable E-Statement Frequency
            accHelpers.selectEStatementFreq(XPathLocators.E_Statement_Freq, data.get("E-Statement Frequency"));
            pause(1000);
        } else {
            System.out.println("Working E-statement");
        }

        // Handle SMS Alert logic
        accHelpers.selectSMSAlert(XPathLocators.Sms_alert, data.get("SMS Alert"));
        pause(1000);
        if (data.get("SMS Alert").equalsIgnoreCase("YES")) {
            accHelpers.selectMobileTelecom(XPathLocators.Mob_telecom, data.get("Mobile Telecom"));
            pause(1000);
        } else {
            System.out.println("Working Sim");

        }


        accHelpers.selectInternetBanking(XPathLocators.Internet_bank, data.get("Internet Banking"));
        pause(1000);

        accHelpers.selectMobileBanking(XPathLocators.Mobile_bank, data.get("Mobile Banking"));
        pause(1000);

        accHelpers.selectAccOfficer(XPathLocators.Acc_officer, data.get("Account Officer"));
        pause(1000);

        // Select Account Operational Type (Signature) only if NOT "JOINTLY"
        if (!"JOINTLY".equalsIgnoreCase(data.get("Account Operational Type"))) {
            accHelpers.selectAccOpTypeSignature(XPathLocators.Acc_Op, data.get("Account Operational Type (Signature)"));
            pause(1000);
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.ACC_KYC_Remarks)))
                .sendKeys(data.get("Account KYC Remarks"));
        pause(1000);

        // Check if Account Operational Type is "JOINTLY"
        if ("JOINTLY".equalsIgnoreCase(data.get("Account Operational Type"))) {
            System.out.println("Handling Jointly Account CNIC Check...");

            // Retrieve CNIC & CIF from Excel
            String cnicNumber = data.get("Joint Id Document No 1");
            String userCIF = data.get("CIF Number");

            // ✅ Enter CNIC Number
            WebElement cnicField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.JOINT_ID_DOCUMENT_TEXT)));
            cnicField.sendKeys(cnicNumber);
            pause(500);

            // ✅ Click Check Button
            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.JOINT_ID_DOCUMENT_BUTTON)));
            checkButton.click();
            pause(2000);

            // ✅ Check for Popup or Success Message
            boolean isPopupVisible = false;
            boolean isSuccessMessageVisible = false;
            boolean isCIFSelected = false;

            try {
                isPopupVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.POPUP_XPATH))).isDisplayed();
            } catch (TimeoutException e) {
                System.out.println("Popup not found.");
            }

            try {
                isSuccessMessageVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPathLocators.SUCCESS_MESSAGE_XPATH))).isDisplayed();
            } catch (TimeoutException e) {
                System.out.println("Success message not found.");
            }

                if (!isPopupVisible && !isSuccessMessageVisible) {
                    Assert.fail("Test Failed: No popup or success message appeared after checking CNIC.");
                }

    //  If popup is visible, check for CIF in table and click radio button
                if (isPopupVisible) {
                    System.out.println("Searching for CIF: " + userCIF);
                    try {
                        // Wait for CIF element to appear
                        By cifLocator = By.xpath(XPathLocators.CIF_TABLE_ROW.replace("{CIF}", userCIF));
                        WebElement cifElement = wait.until(ExpectedConditions.visibilityOfElementLocated(cifLocator));

                        if (cifElement.isDisplayed()) {
                            System.out.println(" CIF Found: " + userCIF);

                            // Now wait for the radio button related to the CIF
                            By radioButtonLocator = By.xpath(XPathLocators.CIF_INPUT_RADIOBUTTON.replace("{CIF}", userCIF));
                            WebElement radioButton = wait.until(ExpectedConditions.elementToBeClickable(radioButtonLocator));

                            // Optional: Use JS for faster interaction
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
                            pause(1000);

                            WebElement selectButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.SELECT_BUTTON)));
                            selectButton.click();
                            pause(2000);
                            isCIFSelected = true;
                        }
                    } catch (TimeoutException | NoSuchElementException e) {
                        System.out.println("CIF not found in table or took too long. Skipping this entry.");
                    }
                }


                // ✅ If success message is visible, continue processing
                if (isSuccessMessageVisible) {
                    System.out.println("Existing to bank - Proceeding with account creation.");
                }

                // ✅ Perform New Steps After CIF Selection or Success Message
                if (isCIFSelected || isSuccessMessageVisible) {
                    accHelpers.selectRelation(XPathLocators.Relation, data.get("Relations"));
                    pause(1000);

                    String filePath = data.get("SSCARD"); // Get file path from Excel
                    uploadSSCardFile(filePath); // Call the simplified function


                }
            }

    }
    public static void uploadSSCardFile(String filePath) {
        try {
            // 1. Find the file input using locator from XPathLocators
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(XPathLocators.SSCARD_FILE_INPUT)
            ));

            // 2. Upload the file
            fileInput.sendKeys(filePath);

            // 3. Click Upload Image button
            WebElement uploadButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(XPathLocators.UPLOAD_BUTTON)
            ));
            uploadButton.click();

            // 4. Wait for success message
            WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(XPathLocators.UPLOAD_SUCCESS_MSG)
            ));

            System.out.println("✅ SSCARD uploaded successfully.");

        } catch (Exception e) {
            System.out.println("❌ SSCARD upload failed.");
            Assert.fail("File upload failed: " + e.getMessage());
        }
    }



    @Then("Submit the form")
    public static void submitTheForm() throws IOException, InterruptedException {
        WebElement contactAccSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Acc_Submit)));
        contactAccSubmitButton.click();

        pause(2000);
        waitUntilSpinnerIsInvisible();
        runThreadsAfterFormSubmission();

        // Check if the toast container appears (indicating an error)
//        try {
//            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
//            WebElement toastContainer = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='toast-container']")));
//
//            if (toastContainer.isDisplayed()) {
//                takeScreenshot("Toast_Error_" + System.currentTimeMillis() + ".png");
//                Assert.fail("Test failed due to error toast message: " + toastContainer.getText().trim());
//            }
//        } catch (TimeoutException e) {
//            // No toast appeared, continue as usual
//        }
    }
        public static void takeScreenshot(String reason) {
            try {
                String timestamp = new SimpleDateFormat("[dd-MMM-yyyy]_[hh-mm-ss a]").format(new Date());

                // Create readable file name
                String fileName = reason.replaceAll("\\s+", "_") + "_" + timestamp + ".png";

                // Take screenshot
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                // Set screenshot folder
                String folderPath = System.getProperty("user.dir") + "/screenshots/";
                new File(folderPath).mkdirs(); // create folder if it doesn't exist

                // Save the file
                File destination = new File(folderPath + fileName);
                FileUtils.copyFile(screenshot, destination);

                System.out.println(" Screenshot saved: " + destination.getAbsolutePath());

            } catch (IOException e) {
                System.err.println(" Could not save screenshot: " + e.getMessage());
            }
        }

    private static void waitUntilSpinnerIsInvisible() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("spinner-overlay")));
        } catch (Exception e) {
            System.out.println("Spinner did not disappear: " + e.getMessage());
        }
    }

    @Then("Send To Supervisor")
    public static void sendToSupervisor() {
        pause(2000);
        WebElement sendSupervisorButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.SendToSupervisor)));
        sendSupervisorButton.click();
        pause(3000);
        WebElement sendSupervisorYESButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.SendToSupervisor_YES)));
        sendSupervisorYESButton.click();
        pause(2000);
        waitUntilSpinnerIsInvisible();
        pause(3000);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(XPathLocators.TRACKING_INFO)));
        trackingCif.extractTrackingInfo();  // Call the method to extract Tracking ID and CIF No

        WebElement approveBackSButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Approve_Back)));
        approveBackSButton.click();
        pause(3000);
    }


    @And("Uploading Files:")
    public static void uploadingFiles() throws IOException {
        pause(2000);  // Adjust this as needed

        // Get the current row data (documents and images)
        Map<String, String> data = currentRowData;
        if (data == null || data.isEmpty()) {
            System.out.println("No documents to upload.");
            return;
        }

        // Continue with your logic as before, including processing image paths
        nationality = currentRowData.get("Nationality");
        List<String> documentUploadOrder = Arrays.asList(
                "ID Document", "Proof of Address", "Proof of Income", "CRS Form", "IRS Form",
                "Signature Speciman Card");

        pause(2000);

        for (String documentName : documentUploadOrder) {
            if (nationality != null && nationality.equalsIgnoreCase("Pakistan") &&
                    (documentName.equals("CRS Form") || documentName.equals("IRS Form"))) {
                System.out.println("Skipping upload of " + documentName + " for nationality: " + nationality);
                continue;  // Skip these documents
            }
            String filePath = data.get(documentName);  // Get the file path for document
            if (filePath != null) {
                // You can check if the document is an image and handle it accordingly
                if (filePath.endsWith(".jpg") || filePath.endsWith(".png")) {
                    // Handle image upload if needed
                    uploadFile(documentName, filePath);
                } else {
                    // Handle regular document upload
                    uploadFile(documentName, filePath);
                }
            } else {
                System.out.println("Skipping upload due to missing file path for: " + documentName);
            }
        }
    }


    public static void uploadFile(String documentName, String filePath) throws IOException {
        // Check if documentName is null
        if (documentName == null || documentName.trim().isEmpty()) {
            System.out.println("ERROR: documentName is null or empty.");
            return;  // Return early if documentName is invalid
        }

        // Log document name for debugging
        System.out.println("Uploading document: " + documentName);

        // Get dynamic locators for the Browse button, File input, and Upload button
        String browseXpath = XPathLocators.getBrowseButtonXpath(documentName);
        String fileInputXpath = XPathLocators.getFileInputXpath(documentName);
        String uploadXpath = XPathLocators.getUploadButtonXpath(documentName);


        // Click the Browse button
        WebElement browseButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(browseXpath)));
        browseButton.click();

        // Wait for the file input to appear, and upload the file
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(fileInputXpath)));
        fileInput.sendKeys(filePath);
        AutoITHandler.closeFileExplorer();

        // Click the Upload button once the file is uploaded
        WebElement uploadButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(uploadXpath)));
        uploadButton.click();
        pause(3000);
        waitUntilSpinnerIsInvisible();
    }

    @And("Go Back To Market Place")
    public static void goBackToMarketPlace() {
        WebElement sendBackButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Back_To_Market_Place)));
        sendBackButton.click();
        pause(3000);
        waitUntilSpinnerIsInvisible();

    }

    @And("Select {string}")
    public static void select(String arg) {
        cifHelpers.selectChecker(arg);
    }

    @And("I navigate to customer information UAT")
    public static void iNavigateToCustomerInformationUAT() {
        WebElement customerInfoButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Customer_UAT)));
        customerInfoButton.click();
        pause(2000);
    }

    @And("I search and click the Tracking Id")
    public static void iSearchAndClickTheTrackingId() {
        trackingCif.findAndClickTrackingID();
        pause(5000);
        System.out.println("DONE");
    }


    @And("Enter Comments {string} and Press {string} and Press {string}")
    public static void enterCommentsAndPressAndPress(String comment, String request, String yes_no) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,700)");

        // Step 1: Enter the comments in the textarea
        requestApprovalSteps.enterComments(comment);
        pause(2000);
        // Step 2: Click the dynamic Request button (Approve or Reject)
        requestApprovalSteps.clickRequestButton(request);
        pause(2000);
        // Step 3: Handle the Yes/No confirmation dialog and click the respective button
        requestApprovalSteps.handleYesNoDialog(yes_no);
        pause(5000);
    }

    @Then("Go Back")
    public static void goBack() {
        WebElement checkerBackButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Checker_Back)));
        checkerBackButton.click();
        pause(2000);
        WebElement signoffButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.Quit)));
        signoffButton.click();
        pause(2000);


    }

    @And("Open T24 environment")
    public static void openT24Environment() {


        driver.navigate().to("http://10.111.201.183:9080/UATENV/servlet/BrowserServlet");
        driver.manage().window().maximize();

    }

    @And("I fill enter Login Credentials for T24:")
    public static void iFillEnterLoginCredentialsForT24(String username, String password) {
        // Locate the username and password fields and fill them
        driver.findElement(By.xpath(XPathLocators.T24_Username)).sendKeys(username);
        pause(1000);
        driver.findElement(By.xpath(XPathLocators.T24_Password)).sendKeys(password);
    }


    @And("I login in T24")
    public static void iLoginInT24() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.T24_SignIn)));
        loginButton.click();
        pause(4000);
    }


    @And("Enter {string}  and Press Tick Button")
    public static void enterAndPressTickButton(String arg0) {
        driver.switchTo().frame(0);
        driver.findElement(By.xpath(XPathLocators.T24_Text)).sendKeys(arg0);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.T24_TickButton))).click();
        // Switch back to the main page
        driver.switchTo().defaultContent();


    }

    @And("Enter CIF No and View it")
    public static void enterCIFNoAndViewIt() {
        // Get the current window handle
        String mainWindowHandle = driver.getWindowHandle();

        // Wait for the new window or tab to open
        wait.until(ExpectedConditions.numberOfWindowsToBe(2)); // Wait for 2 windows

        // Get all window handles
        Set<String> allWindowHandles = driver.getWindowHandles();

        // Switch to the new window
        for (String windowHandle : allWindowHandles) {
            if (!windowHandle.equals(mainWindowHandle)) {
                driver.switchTo().window(windowHandle);
                System.out.println("Switched to new window.");

                // Get the CIF No from Checker_Tracking_CIF
                String cifNo = trackingCif.getCifNo(); // Use the getter method to retrieve CIF No

                if (cifNo == null || cifNo.isEmpty()) {
                    System.out.println("CIF No is not available.");
                    return;
                }

                // Enter the CIF No into the customer field
                WebElement customerIdField = driver.findElement(By.xpath(XPathLocators.T24_Customer_Text));
                customerIdField.sendKeys(cifNo);
                WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPathLocators.T24_View_Customer)));
                viewButton.click();
                pause(10000);
                // Close the new window after viewing the customer details
                driver.close();
                System.out.println("New window closed.");

                // Switch back to the main window
                driver.switchTo().window(mainWindowHandle);
                System.out.println("Switched back to the main window.");
                break;
            }
        }
    }

    public static void runThreadsAfterFormSubmission() {
        try {
            // Create and start Thread t1 (to simulate page load)
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Thread t1 started - Page loaded.");
                    // You can add your logic here, such as opening a webpage with WebDriver
                    // Example: driver.get("http://example.com");
                }
            });
            t1.start(); // Start thread t1

            // Create and start Thread t2 (to simulate pressing Enter key to dismiss pop-up)
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Wait for 5 seconds to simulate the pop-up appearing
                        Thread.sleep(5000);

                        // Use Robot class to simulate pressing Enter key
                        Robot robot = new Robot();
                        robot.keyPress(KeyEvent.VK_ENTER);  // Press the Enter key
                        robot.keyRelease(KeyEvent.VK_ENTER); // Release the Enter key

                        System.out.println("Thread t2 started - Pop-up dismissed.");
                    } catch (InterruptedException | AWTException e) {
                        e.printStackTrace(); // Handle InterruptedException or AWTException
                    }
                }
            });
            t2.start(); // Start thread t2

            // Wait for both threads to complete before continuing
            t1.join(); // Ensure the main thread waits for t1 to finish
            t2.join(); // Ensure the main thread waits for t2 to finish

            System.out.println("Both threads have completed.");

        } catch (InterruptedException e) {
            e.printStackTrace(); // Handle any interruptions
        }
    }



    @And("INIT")
    public static void init() throws IOException {
        // Initialize ExcelHelper with the Excel file path
        ExcelHelper.init("C:\\Users\\Shahroze.Janjua\\Desktop\\Automation2.xlsx");


    }

    @And("Perform Test")
    public static void performTest() throws InterruptedException, IOException {
        while (ExcelHelper.hasNextRow()) {
            perform();
        driver.close();
        }
    }


        private static void perform() throws InterruptedException, IOException {
            currentRowData = ExcelHelper.getNextRowData();

            if (currentRowData == null || currentRowData.isEmpty()) {
                throw new RuntimeException("No data found in the Excel file or currentRowData is null.");
            }


                try {
                    // Step 1: Navigate to the login page
                    i_am_on_the_login_page();

                    // Step 2: Fill in login information
                    iFillInTheLoginInformationAnd("shahroze.janjua", "Nuzhat.12");

                    // Step 3: Log in
                    i_log_in();

                    // Step 4: Navigate to customer information
                    i_navigate_to_customer_information();

                    // Step 5: Navigate to CIF
                    i_navigate_to_cif();

                    // Step 6: Fill in customer information
                    i_fill_in_the_customer_information();
                    checkForToast(); // Check for toast after this step

                    // Step 7: Click Next on Customer Information
                    clickOnCUST_INFO_Next();
                    checkForToast(); // Check for toast after this step

                    // Step 8: Fill in PEP customer demographic
                    iFillInThePEPCustomerDemographic();
                    checkForToast(); // Check for toast after this step

                    // Step 9: Click Next on PEP
                    clickOnPEP_Next();
                    checkForToast(); // Check for toast after this step

                    // Step 10: Fill in contact details
                    iFillInTheContactDetail();
                    checkForToast(); // Check for toast after this step

                    // Step 11: Click Next on Contact Details
                    clickOnCONT_DET_Next();
                    checkForToast(); // Check for toast after this step

                    // Step 12: Fill in next of kin details
                    iFillInTheNextOfKinDetail();
                    checkForToast(); // Check for toast after this step

                    // Step 13: Click Next on Next of Kin
                    clickOnKIN_Next();
                    checkForToast(); // Check for toast after this step

                    // Step 14: Fill in account details
                    iFillInTheAccountDetail();
                    checkForToast(); // Check for toast after this step

                    // Step 15: Submit the form
                    submitTheForm();
                    checkForToast(); // Check for toast after this step

                    // Step 16: Upload files
                    uploadingFiles();
                    checkForToast(); // Check for toast after this step

                    // Step 17: Send to supervisor
                    sendToSupervisor();
                    checkForToast(); // Check for toast after this step

                    // Step 18: Go back to marketplace
                    goBackToMarketPlace();
                    checkForToast(); // Check for toast after this step

                    // Step 19: Select CHECKER role
                    select("CHECKER");
                    checkForToast(); // Check for toast after this step

                    // Step 20: Navigate to customer information in UAT
                    iNavigateToCustomerInformationUAT();
                    checkForToast(); // Check for toast after this step

                    // Step 21: Search and click the tracking ID
                    iSearchAndClickTheTrackingId();
                    checkForToast(); // Check for toast after this step

                    // Step 22: Enter comments and approve
                    enterCommentsAndPressAndPress("I have Approved", "Request Approve", "YES");
                    checkForToast(); // Check for toast after this step

                    // Step 23: Go back
                    goBack();
                    checkForToast(); // Check for toast after this step

                } catch (Exception e) {
                    // Capture screenshot and fail the test in case of any exception
                    takeScreenshot("Workflow_Error_" + System.currentTimeMillis() + ".png");
                    Assert.fail("Test failed due to exception: " + e.getMessage());
                }


            openT24Environment();
            iFillEnterLoginCredentialsForT24("NADEEM01", "NADEEM01");
            iLoginInT24();
            enterAndPressTickButton("CUSTOMER");
            enterCIFNoAndViewIt();

            System.out.println("Test cycle complete----------------------------------------------------------------------------");
        }

    private static final Object lock = new Object();

    // This step will be used to notify the system to proceed to the next row
    @Then("notify to proceed to the next row")
    public static void notifyToProceedToTheNextRow() {
        synchronized (lock) {
            // Notify after processing current row, not before
            ExcelHelper.notifyNextRow();
        }
    }

    @And("Close it")
    public static void closeIt() throws IOException, InterruptedException {

        if (driver != null) {
            driver.quit();
        }
    }
        public static void checkForToast() {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(1));

                List<WebElement> toastContainers = shortWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//div[@role='alertdialog' and contains(@class, 'toast-message')]")));

                for (WebElement toast : toastContainers) {
                    if (toast.isDisplayed()) {
                        String toastMessage = toast.getText().trim();
                        System.out.println("Toast message detected: " + toastMessage);

                        takeScreenshot("Toast_Error_");
                        Assert.fail("Test failed due to toast message: " + toastMessage);
                    }
                }
            } catch (TimeoutException e) {
                System.out.println("No toast message appeared. Continuing.");
            } catch (Exception e) {
                System.out.println("Error checking toast: " + e.getMessage());
            }
        }



//    public static void main(String[] args) throws IOException, InterruptedException {
//        try {
//            init();
//            while (ExcelHelper.hasNextRow()) {
//                try {
//                    perform();
//                    notifyToProceedToTheNextRow();
//
//                } catch (Exception e) {
//                    takeScreenshot(driver, "exception_" + System.currentTimeMillis());
//                    throw e;
//                }
//            }
//        } finally {
//            if (driver != null) {
//                driver.quit();
//            }
//        }
//    }

}

