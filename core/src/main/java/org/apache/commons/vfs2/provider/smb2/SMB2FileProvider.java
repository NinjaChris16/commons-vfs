package org.apache.commons.vfs2.provider.smb2;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.share.DiskShare;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.GenericFileName;
import org.apache.commons.vfs2.util.UserAuthenticatorUtils;
import org.apache.hadoop.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SMB2FileProvider extends AbstractOriginatingFileProvider {
    private SMB2FileSystem fileSystem;

    private static final UserAuthenticationData.Type[] AUTHENTICATOR_TYPES = new UserAuthenticationData.Type[] {
            UserAuthenticationData.USERNAME, UserAuthenticationData.PASSWORD
    };

    static final Collection<Capability> capabilities = Collections.unmodifiableCollection(Arrays.asList(
            Capability.CREATE,
            Capability.DELETE,
            Capability.RENAME,
            Capability.GET_TYPE,
            Capability.LIST_CHILDREN,
            Capability.READ_CONTENT,
            Capability.DIRECTORY_READ_CONTENT,
            Capability.URI,
            Capability.WRITE_CONTENT,
            Capability.APPEND_CONTENT
            ));

    @Override
    public FileObject findFile(final FileObject baseFile, final String stringURI, final FileSystemOptions fileSystemOptions) throws FileSystemException {
        FileName name;
        final URI Uri;

        try {
            Uri = new URI(stringURI);
            name = new SMB2FileName(Uri, FileType.FILE_OR_FOLDER);
        }
        catch (final Exception exc) {
            throw new FileSystemException("vfs.provider/invalid-absolute-uri.error", stringURI, exc);
        }

        if(fileSystem == null) {

            doCreateFileSystem(Uri, null);
        }
        // TODO add case if baseFile is null

        // Locate the file
        return fileSystem.resolveFile(Uri.getPath());
        //return baseFile.resolveFile(name.getPath());
    }

    @Override
    protected FileSystem doCreateFileSystem(FileName name, FileSystemOptions fileSystemOptions) throws FileSystemException {
        UserAuthenticationData authData = new UserAuthenticationData();

        try {
            authData = UserAuthenticatorUtils.authenticate(fileSystemOptions, AUTHENTICATOR_TYPES); // TODO use this to abstract authentication
            URI uri = new URI(name.getURI());
            DiskShare smbClient = SMB2ClientFactory.createConnection(uri.getHost(), uri.getUserInfo().substring(0, uri.getUserInfo().indexOf(":")), uri.getUserInfo().substring(uri.getUserInfo().indexOf(":")+1), name.getPath());
            fileSystem = new SMB2FileSystem(name, smbClient, fileSystemOptions);
            return fileSystem;
        } catch (final URISyntaxException e) {
            System.out.printf("%s\n", e.getMessage());
            throw new FileSystemException("vfs.provider.smb2/connect.error", name, e);
        }
    }

    protected FileSystem doCreateFileSystem(URI uri, FileSystemOptions fileSystemOptions) throws FileSystemException {
        UserAuthenticationData authData = new UserAuthenticationData();

        authData = UserAuthenticatorUtils.authenticate(fileSystemOptions, AUTHENTICATOR_TYPES); // TODO use this to abstract authentication
        DiskShare smbClient = SMB2ClientFactory.createConnection(uri.getHost(), uri.getUserInfo().substring(0, uri.getUserInfo().indexOf(":")), uri.getUserInfo().substring(uri.getUserInfo().indexOf(":")+1), uri.getPath().substring(0, uri.getPath().indexOf("/", 1)));
        fileSystem = new SMB2FileSystem(new SMB2FileName(uri, FileType.FILE_OR_FOLDER), smbClient, fileSystemOptions);
        return fileSystem;
    }

    @Override
    public Collection<Capability> getCapabilities() {
        return capabilities;
    }
}
