import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Whatsapp {

    public static String targetName = "";
    private static final String BOT_TOKEN = "OTMwNTkyNTQ1OTkyNDc4NzUw.Yd4H2A.fxPHop3b_y2LoC_PxoV7u7Z5EQ0";
    private static final String GENERAL_CHANNEL_ID = "930593243354267670";
    private static final String RANDOM_CHANNEL_ID = "930600300690178138";

    public static int spamSpeed = 1;
    public static int msgId = 0;

    private static boolean isStoreTime = true;
    public static boolean isMsgSpam = false;
    public static boolean isSeqMsgs = false;
    public static boolean isSendMsgOnce = false;
    public static boolean isSendMsg = false;
    public static boolean isSwapTarget = false;
    public static boolean isStartBot = false;
    private static boolean isMsgIds = true;

    public static ArrayList<String> targetMsg = new ArrayList<>();
    public static ArrayList<String> targetChatMsgList = new ArrayList<>();
    public static ArrayList<String> clientChatMsgList = new ArrayList<>();
    public static ArrayList<String> tempSeqList = new ArrayList<>();

    private static LocalDateTime startTime = LocalDateTime.now();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static ChromeDriver driver;
    private static JDA jda;




    public static void main(String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> driver.quit(), "Shutdown-thread"));

        GUI gui = new GUI();

        while (true) {
            Thread.sleep(spamSpeed);

            if (isStartBot) {
                System.out.println("Starting bot...");
                System.setProperty("webdriver.chrome.driver", "resources/chromedriver_win32/chromedriver.exe");
                driver = new ChromeDriver();

                JDABuilder builder = JDABuilder.createDefault(BOT_TOKEN);
                jda = builder.build();
                jda.awaitReady();

                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                driver.get("https://web.whatsapp.com/");

                searchTarget(driver);
                enterChat(driver);
                openProfile(driver);
                isStartBot = false;
            }

            if (gui.startBtn.getText().equalsIgnoreCase("Pause Bot")) {

                isMsgIds();
                sendReply();
                isSwapTarget();
                messageSpamming();
                System.out.println(getStatus(driver));

                if (!getStatus(driver).isEmpty() && (getStatus(driver).equalsIgnoreCase("Online") || getStatus(driver).equalsIgnoreCase("Typing..."))) {
                    if (isStoreTime) {
                        startTime = LocalDateTime.now();
                        isStoreTime = false;
                    }

                    if ((isSendMsg && !isMsgSpam && isSendMsgOnce) || (isSendMsgOnce && isMsgSpam)) {
                        closeProfile(driver, 20);
                        sendChat(driver);
                        openProfile(driver);
                        isSendMsg = false;
                    }

                    gui.errorMsg.setForeground(Color.GREEN);
                    gui.errorMsg.setText(LocalDateTime.now().format(formatter) + ": " + targetName + " is online!");
                } else {
                    LocalDateTime endTime = LocalDateTime.now();
                    Duration duration = Duration.between(startTime, endTime);
                    if (duration.getSeconds() > spamSpeed && !isStoreTime) {
                        jda.getTextChannelById(RANDOM_CHANNEL_ID).sendMessage("***" + targetName + "* was online!**" +
                                "\n**Duration:** " + durationFormat(duration) +
                                "\n**Went online:** " + startTime.format(formatter) +
                                "\n**Went offline:** " + endTime.format(formatter)).queue();

                        isStoreTime = true;
                    }

                    isSendMsg = true;

                    startTime = LocalDateTime.now();
                    gui.errorMsg.setForeground(Color.GREEN);
                    if (isMsgSpam && !isSendMsgOnce) {
                        gui.errorMsg.setText("Spamming Target: " + targetName);
                    } else {
                        gui.errorMsg.setText(LocalDateTime.now().format(formatter) + ": Waiting for " + targetName + "'s status!");
                    }

                }
            }
        }
    }

    private static void messageSpamming() {
        if (isMsgSpam && !isSendMsgOnce) {
            String EXIT_XPATH = "//button[@class='_18eKe']//span";
            if (!isElementPresent(driver, 0, EXIT_XPATH)) {
                sendChat(driver);
            } else {
                closeProfile(driver, 0);
            }
        }
    }

    private static void isSwapTarget() {
        if (isSwapTarget) {
            clearSearch(driver);
            searchTarget(driver);
            enterChat(driver);
            openProfile(driver);
            isSwapTarget = false;
        }
    }

    private static void isMsgIds() {
        if (isMsgIds) {
            for (WebElement i : driver.findElements(By.xpath("//div[contains(@class, 'message-in')]//span[@dir='ltr']"))) {
                setAttribute(i, "id", String.valueOf(msgId++));
            }
            isMsgIds = false;
        }
    }

    private static void sendReply() {
        final String MESSAGE_XPATH = "//div[contains(@class, 'message-in')]//span[contains(@dir, 'ltr') and not(@id)]";
        final String CHAT_BOX_XPATH = "//div[@class='_6h3Ps']//div[@title='Type a message']";

        if (driver.findElements(By.xpath(MESSAGE_XPATH)).size() > 0) {
            if (targetChatMsgList.contains(getTargetChatMessage(driver).toLowerCase())) {
                for (int i = 0; i < targetChatMsgList.size(); i++) {
                    if (targetChatMsgList.get(i).equalsIgnoreCase(getTargetChatMessage(driver))) {
                        closeProfile(driver, 0);
                        driver.findElement(By.xpath(CHAT_BOX_XPATH)).click();
                        driver.findElement(By.xpath(CHAT_BOX_XPATH)).sendKeys(clientChatMsgList.get(i));
                        driver.findElement(By.xpath(CHAT_BOX_XPATH)).sendKeys(Keys.RETURN);
                        setAttribute(driver.findElement(By.xpath(MESSAGE_XPATH)), "id", String.valueOf(msgId++));
                        break;
                    }
                }
            } else {
                setAttribute(driver.findElement(By.xpath(MESSAGE_XPATH)), "id", String.valueOf(msgId++));
            }
        }
    }

    private static String durationFormat(Duration duration) {
        return String.format("%d:%02d:%02d", duration.toHours(), duration.toMinutes(), duration.getSeconds());
    }

    private static String getTargetChatMessage(ChromeDriver driver) {
        final String CHAT_MESSAGE = "//div[contains(@class, 'message-in')][last()]//span[contains(@dir, 'ltr') and not(@id)]";

        return isElementPresent(driver, 0, CHAT_MESSAGE) ? driver.findElement(By.xpath(CHAT_MESSAGE)).getText() : "Can't find element";
    }

    private static void searchTarget(ChromeDriver driver) {
        final String SEARCHBAR_XPATH = "//*[@id=\"side\"]/div[1]/div/div/div[2]/div/div[2]";

        if (isElementClickable(driver, 20, SEARCHBAR_XPATH)) {
            driver.findElement(By.xpath(SEARCHBAR_XPATH)).click();
            driver.findElement(By.xpath(SEARCHBAR_XPATH)).sendKeys(targetName);
        }
    }

    private static void clearSearch(ChromeDriver driver) {
        final String CLEAR_X_XPATH = "//*[@id=\"side\"]/div[1]/div/span/button";

        if (isElementPresent(driver, 1, CLEAR_X_XPATH) && isElementClickable(driver, 1, CLEAR_X_XPATH)) {
            driver.findElement(By.xpath(CLEAR_X_XPATH)).click();
        }
    }

    private static void enterChat(ChromeDriver driver) {
        final String CHAT_NAME_XPATH = "//span[@title='" + targetName + "']";

        if (isElementClickable(driver, 20, CHAT_NAME_XPATH)) {
            driver.findElement(By.xpath(CHAT_NAME_XPATH)).click();
        }
    }

    private static void openProfile(ChromeDriver driver) {
        final String TOP_NAME_XPATH = "//*[@id=\"main\"]/header/div[2]/div/div/span";

        if (isElementClickable(driver, 20, TOP_NAME_XPATH)) {
            driver.findElement(By.xpath(TOP_NAME_XPATH)).click();
        }
    }

    private static String getStatus(ChromeDriver driver) {
        final String STATUS_XPATH = "//header[@class='_23P3O']//span";

        return isElementPresent(driver, 0, STATUS_XPATH) ? driver.findElement(By.xpath(STATUS_XPATH)).getText() : "";
    }

    private static void closeProfile(ChromeDriver driver, int timeout) {
        final String EXIT_XPATH = "//button[@class='_18eKe']//span";

        if (isElementPresent(driver, timeout, EXIT_XPATH) && isElementClickable(driver, timeout, EXIT_XPATH)) {
            driver.findElement(By.xpath(EXIT_XPATH)).click();
        }
    }

    private static void sendChat(ChromeDriver driver) {
        final String CHAT_BOX_XPATH = "//div[@class='_6h3Ps']//div[@title='Type a message']";
        String[] atName;

        if (isElementPresent(driver, 20, CHAT_BOX_XPATH)) {
            driver.findElement(By.xpath(CHAT_BOX_XPATH)).click();

            if (targetMsg.size() > 1) {
                System.out.println("Current message: " + targetMsg.get(0));
                if (!tempSeqList.isEmpty() && tempSeqList.get(0).contains("@") && tempSeqList.get(0).split("@").length > 0) {
                    atName = tempSeqList.get(0).split(" ", 0);
                    driver.findElement(By.xpath(CHAT_BOX_XPATH)).sendKeys(atName[0]);
                    driver.findElement(By.xpath(CHAT_BOX_XPATH)).sendKeys(Keys.RETURN);
                }

                if (isSeqMsgs) {
                    if (tempSeqList.isEmpty()) {
                        tempSeqList = new ArrayList<>(targetMsg);
                    } else {
                        String replaced = tempSeqList.get(0).replaceAll("(?<=^|\\W)@\\w+", "");
                        driver.findElement(By.xpath(CHAT_BOX_XPATH)).sendKeys(replaced);
                        tempSeqList.remove(0);
                    }
                } else {
                    Random random = new Random();
                    driver.findElement(By.xpath(CHAT_BOX_XPATH)).sendKeys(targetMsg.get(random.nextInt(targetMsg.size())));
                }
            } else {
                driver.findElement(By.xpath(CHAT_BOX_XPATH)).sendKeys(targetMsg.get(0));
            }
            driver.findElement(By.xpath(CHAT_BOX_XPATH)).sendKeys(Keys.RETURN);
        }
    }

    private static void setAttribute(WebElement element, String attName, String attValue) {
        driver.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                element, attName, attValue);
    }

    private static boolean isElementPresent(ChromeDriver driver, int timeout, String xPath) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isElementClickable(ChromeDriver driver, int timeout, String xPath) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
