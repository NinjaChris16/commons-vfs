package org.apache.commons.vfs2.provider.smb2;

import com.hierynomus.smbj.share.DiskEntry;
import com.hierynomus.smbj.transport.TransportException;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;

import java.net.URI;

public class SMB2FileName extends AbstractFileName {
    private String hostName;
    private String shareName;

    public SMB2FileName(String hostName, String shareName, String absPath, FileType type) {
        super("smb", absPath, type);
        this.hostName = hostName;
        this.shareName = shareName;
    }

    public SMB2FileName(URI uri, FileType type) {
        super("smb", uri.getPath().substring(uri.getPath().indexOf("/", 1)), type);
        this.hostName = uri.getHost();
        this.shareName = uri.getPath().substring(1, uri.getPath().indexOf("/", 1));
    }

    @Override
    public FileName createName(String absPath, FileType type) {
        return new SMB2FileName(hostName, shareName, getPath(), getType());
    }

    @Override
    protected void appendRootUri(StringBuilder buffer, boolean addPassword) {
        buffer.append(getScheme());
        buffer.append("://");
        buffer.append(hostName);
        buffer.append("/");
        buffer.append(shareName);
    }
}
