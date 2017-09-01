package org.apache.commons.vfs2.provider.smb2.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.AbstractVfsTestCase;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.smb2.SMB2FileObject;
import org.apache.commons.vfs2.provider.smb2.SMB2FileProvider;
import org.apache.commons.vfs2.provider.smb2.SMB2FileSystem;
import org.apache.commons.vfs2.test.AbstractProviderTestCase;
import org.apache.commons.vfs2.test.AbstractProviderTestConfig;
import org.apache.commons.vfs2.test.ProviderTestConfig;
import org.apache.commons.vfs2.test.ProviderTestSuite;

import java.net.URI;
import java.net.URISyntaxException;

public class SMB2ProviderTestCase extends AbstractProviderTestConfig implements ProviderTestConfig {
    private static String serverName = "sd-test";  // The server name for testing purposes
    private static String userInfo = "WORKGROUP\\user:easypass"; // The domain\\user:password combo for logging in to server
    private static String folderPath = "/share/folder"; // The path to the root folder for testing purposes beginning with the share name
    //private static String TEST_URI = "";

    final TestSuite suite = new TestSuite();

    public static Test suite() throws Exception {
        return new ProviderTestSuite(new SMB2ProviderTestCase());
    }

    @Override
    public void prepare(final DefaultFileSystemManager manager) throws Exception {
        manager.addProvider("smb", new SMB2FileProvider());
    }

    public FileObject getBaseTestFolder(final FileSystemManager manager) throws Exception {
        return manager.resolveFile(buildURI().toString());
    }

    private URI buildURI() throws URISyntaxException {
        return new URI("smb", userInfo, serverName, 445, folderPath, "", "");
    }
}
