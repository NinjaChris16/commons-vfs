package org.apache.commons.vfs2.provider.smb2;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import org.apache.commons.vfs2.FileSystemException;

import java.io.IOException;
import java.net.URI;

public class SMB2ClientFactory {
    private SMB2ClientFactory(){}

    public static DiskShare createConnection(String serverName, String userName, String password, String shareName) throws FileSystemException {
        SMBClient client = new SMBClient();
        try(Connection connection = client.connect(serverName)) {
            AuthenticationContext auth = new AuthenticationContext(userName.substring(userName.indexOf("\\")+1), password.toCharArray(), userName.substring(0, userName.indexOf("\\")));    // Domain is string before the '\' character in the username, username is string after.
            Session session = connection.authenticate(auth);
            DiskShare share = (DiskShare) session.connectShare(shareName.substring(0, shareName.indexOf("/")));
            return share;
        } catch(IOException e) {
            throw new FileSystemException(e.getCause());
        } catch(Exception e) {}

        return null;
    }

    public static DiskShare createConnection(URI uri) throws FileSystemException {
        String userInfo = uri.getUserInfo();
        int endOfUserName = userInfo.indexOf(":");
        return createConnection(uri.getHost(), userInfo.substring(endOfUserName), userInfo.substring(endOfUserName+1), uri.getPath());
    }
}
