package com.alexandrov.utils;

import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class PlaywrightDriver {
    private static Playwright playwright;
    private static Browser browser;

    private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private static final ThreadLocal<Page> page = new ThreadLocal<>();

    public static void init() {
        if (browser == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setSlowMo(100)
            );
        }
        context.set(browser.newContext());
        page.set(context.get().newPage());
    }

    public static Page getPage() {
        if (page.get() == null) {
            init();
        }
        return page.get();
    }

    public static void close() {
        if (context.get() != null) {
            context.get().close();
            context.remove();
            page.remove();
        }
    }

    public static byte[] takeScreenshot() {
        return page.get().screenshot();
    }

    public static void setDownloadPath(String path) {
        context.get().close();
        context.set(browser.newContext(new Browser.NewContextOptions()
                .setAcceptDownloads(true)));
        page.set(context.get().newPage());
    }
}