package com.androidnexus.parser;

import com.androidnexus.model.Notification;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationParserTest {

    @Test
    public void testParseNotificationsList() {
        String mockOutput = "Notification List:\n" +
                "  NotificationRecord{0x12a3b4c key=0|com.whatsapp|1001|null|10123}\n" +
                "    pkg=com.whatsapp user=0 id=1001 tag=null\n" +
                "    flags=0x10\n" +
                "    Notification:\n" +
                "      title=John Doe\n" +
                "      text=Hey, are you free?\n" +
                "      when=1705312200000\n" + // 2024-01-15 15:30:00 UTC approximately
                "  NotificationRecord{0x56d7e8f key=0|com.google.android.gm|2002|tag_mail|10124}\n" +
                "    pkg=com.google.android.gm user=0 id=2002 tag=tag_mail\n" +
                "    flags=0x22\n" + // FLAG_ONGOING_EVENT = 0x2, FLAG_NO_CLEAR = 0x20
                "    Notification:\n" +
                "      title=Gmail Update\n" +
                "      text=Important Security Alert\n" +
                "      when=0\n";

        List<Notification> list = NotificationParser.parse(mockOutput);

        assertEquals(2, list.size());

        // WhatsApp Notification
        Notification n1 = list.get(0);
        assertEquals("0|com.whatsapp|1001|null|10123", n1.getKey());
        assertEquals("com.whatsapp", n1.getPackageName());
        assertEquals("John Doe", n1.getTitle());
        assertEquals("Hey, are you free?", n1.getText());
        assertNull(n1.getTag());
        assertEquals(1001, n1.getId());
        assertFalse(n1.isOngoing());
        assertTrue(n1.isClearable()); // flags = 0x10 (not ongoing, not no-clear)
        assertNotNull(n1.getTimestamp());

        // Gmail Notification
        Notification n2 = list.get(1);
        assertEquals("0|com.google.android.gm|2002|tag_mail|10124", n2.getKey());
        assertEquals("com.google.android.gm", n2.getPackageName());
        assertEquals("Gmail Update", n2.getTitle());
        assertEquals("Important Security Alert", n2.getText());
        assertEquals("tag_mail", n2.getTag());
        assertEquals(2002, n2.getId());
        assertTrue(n2.isOngoing()); // flags = 0x22 has 0x2 (ongoing)
        assertFalse(n2.isClearable()); // flags = 0x22 has 0x20 (no clear)
        assertNull(n2.getTimestamp()); // when=0 should be null
    }

    @Test
    public void testParseEmptyOutput() {
        List<Notification> list = NotificationParser.parse("");
        assertTrue(list.isEmpty());
    }

    @Test
    public void testParseNullOutput() {
        List<Notification> list = NotificationParser.parse(null);
        assertTrue(list.isEmpty());
    }
}
