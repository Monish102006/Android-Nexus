package com.androidnexus.model;

/**
 * Represents a file or directory on the connected Android device.
 *
 * Populated by {@link com.androidnexus.parser.AndroidFileParser} from the
 * output of {@code adb shell ls -l}.
 *
 * Example ls -l output line:
 *   -rw-rw---- 1 root sdcard_rw  1048576 2024-01-15 10:30 document.pdf
 *   drwxrwx--x 2 root sdcard_rw     4096 2024-01-15 10:30 Photos
 *
 * Fields:
 *   name      ← filename from columns 7+ (handles spaces in names)
 *   path      ← full path constructed as directory + "/" + name
 *   directory ← true if permissions string starts with "d"
 *   size      ← file size in bytes from column 4
 *
 * Future expansion (Module 5 enhancement):
 *   - permissions (e.g. "rwxrwxrwx")
 *   - owner / group
 *   - lastModified timestamp
 *   - file extension
 *   - human-readable size
 */
public class AndroidFile {

    private String name;
    private String path;
    private boolean directory;
    private long size;
    private String permissions;
    private String owner;
    private String group;
    private String lastModified;
    private String extension;
    private String humanReadableSize;
    private boolean symlink;
    private String symlinkTarget;

    public AndroidFile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getHumanReadableSize() {
        return humanReadableSize;
    }

    public void setHumanReadableSize(String humanReadableSize) {
        this.humanReadableSize = humanReadableSize;
    }

    public boolean isSymlink() {
        return symlink;
    }

    public void setSymlink(boolean symlink) {
        this.symlink = symlink;
    }

    public String getSymlinkTarget() {
        return symlinkTarget;
    }

    public void setSymlinkTarget(String symlinkTarget) {
        this.symlinkTarget = symlinkTarget;
    }

    @Override
    public String toString() {
        return "AndroidFile{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", directory=" + directory +
                ", size=" + size +
                ", permissions='" + permissions + '\'' +
                ", owner='" + owner + '\'' +
                ", group='" + group + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", extension='" + extension + '\'' +
                ", humanReadableSize='" + humanReadableSize + '\'' +
                ", symlink=" + symlink +
                ", symlinkTarget='" + symlinkTarget + '\'' +
                '}';
    }
}