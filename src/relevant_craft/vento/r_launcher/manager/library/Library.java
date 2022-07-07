package relevant_craft.vento.r_launcher.manager.library;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Library {

    public String id;
    public String name;
    public File path;
    public URL url;
    public long size;
    public boolean isNatives;
    public boolean isMainJar;
    public boolean isPacked;
    public String natives_os;
    public String version;

    public Library(String _id, String _path, String _url, long _size, boolean _isNatives, boolean _isMainJar, boolean _isPacked, String _natives_os, String _version) {
        id = _id;
        name = LibraryManager.nameFromUrl(_path, _url, _isMainJar, _version);
        path = new File(LibraryManager.correctPath(_path, _isMainJar, _isNatives, _version, true));
        try { url  = new URL(_url); } catch (MalformedURLException e) {}
        size = _size;
        isNatives = _isNatives;
        isMainJar = _isMainJar;
        isPacked = _isPacked;
        natives_os = _natives_os;
        version = _version;
    }

    public Library(String _id, String _path, boolean modifyPath, String _url, long _size, boolean _isNatives, boolean _isMainJar, boolean _isPacked, String _natives_os, String _version) {
        id = _id;
        name = LibraryManager.nameFromUrl(_path, _url, _isMainJar, _version);
        if (modifyPath) {
            path = new File(LibraryManager.correctPath(_path, _isMainJar, _isNatives, _version, true));
        } else {
            path = new File(_path);
        }
        try { url  = new URL(_url); } catch (MalformedURLException e) {}
        size = _size;
        isNatives = _isNatives;
        isMainJar = _isMainJar;
        isPacked = _isPacked;
        natives_os = _natives_os;
        version = _version;
    }
}