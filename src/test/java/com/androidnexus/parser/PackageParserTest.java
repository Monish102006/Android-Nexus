package com.androidnexus.parser;

import com.androidnexus.model.AndroidApplication;
import com.androidnexus.model.ApplicationType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PackageParserTest {

    @Test
    public void testParsePackageList() {
        String mockOutput = "package:/system/app/Gallery/Gallery.apk=com.android.gallery\n" +
                "package:/data/app/~~u2g==/com.whatsapp-Ww==/base.apk=com.whatsapp\n" +
                "invalid line\n" +
                "package:/data/app/com.facebook.katana/base.apk=com.facebook.katana\n";

        List<AndroidApplication> apps = PackageParser.parsePackageList(mockOutput);

        assertEquals(3, apps.size());

        // System App
        AndroidApplication app1 = apps.get(0);
        assertEquals("com.android.gallery", app1.getPackageName());
        assertEquals("/system/app/Gallery/Gallery.apk", app1.getApkPath());
        assertEquals("Gallery", app1.getAppName());
        assertEquals(ApplicationType.SYSTEM, app1.getType());
        assertTrue(app1.isEnabled());

        // User App 1
        AndroidApplication app2 = apps.get(1);
        assertEquals("com.whatsapp", app2.getPackageName());
        assertEquals("/data/app/~~u2g==/com.whatsapp-Ww==/base.apk", app2.getApkPath());
        assertEquals("Whatsapp", app2.getAppName());
        assertEquals(ApplicationType.USER, app2.getType());

        // User App 2
        AndroidApplication app3 = apps.get(2);
        assertEquals("com.facebook.katana", app3.getPackageName());
        assertEquals("Katana", app3.getAppName());
        assertEquals(ApplicationType.USER, app3.getType());
    }

    @Test
    public void testParsePackageDetails() {
        String mockOutput = "Activity Resolver Table:\n" +
                "  versionName=2.24.5.78\n" +
                "  versionCode=24057803 minSdk=24 targetSdk=34\n" +
                "  installerPackageName=com.android.vending\n" +
                "  pkgFlags=[ SYSTEM HAS_CODE ]\n";

        AndroidApplication app = new AndroidApplication();
        app.setPackageName("com.whatsapp");

        PackageParser.parsePackageDetails(mockOutput, app);

        assertEquals("2.24.5.78", app.getVersionName());
        assertEquals(24057803, app.getVersionCode());
        assertEquals("24", app.getMinSdk());
        assertEquals("34", app.getTargetSdk());
        assertEquals("com.android.vending", app.getInstaller());
    }

    @Test
    public void testParsePackageDetailsSideloaded() {
        String mockOutput = "  versionName=1.0\n" +
                "  versionCode=1\n" +
                "  installerPackageName=null\n";

        AndroidApplication app = new AndroidApplication();
        PackageParser.parsePackageDetails(mockOutput, app);

        assertEquals("1.0", app.getVersionName());
        assertEquals(1, app.getVersionCode());
        assertEquals("Sideloaded/Direct", app.getInstaller());
    }

    @Test
    public void testParsePackageDetailsEmpty() {
        AndroidApplication app = new AndroidApplication();
        app.setVersionCode(5);
        
        PackageParser.parsePackageDetails("", app);
        assertEquals(5, app.getVersionCode());
    }
}
