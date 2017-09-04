package org.apache.commons.vfs2.provider.smb2;

import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.share.DiskEntry;
import com.hierynomus.smbj.share.DiskShare;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

import java.util.*;

import static com.hierynomus.msdtyp.AccessMask.GENERIC_ALL;
import static com.hierynomus.msfscc.FileAttributes.FILE_ATTRIBUTE_NORMAL;
import static com.hierynomus.mssmb2.SMB2CreateOptions.FILE_DIRECTORY_FILE;

public class SMB2FileSystem extends AbstractFileSystem {
    private final DiskShare client;

    protected SMB2FileSystem(final FileName rootName, final DiskShare client, final FileSystemOptions fileSystemOptions) {
        super(rootName, null, fileSystemOptions);
        this.client = client;
    }

    @Override
    protected void addCapabilities(final Collection<Capability> capabilities) {
        capabilities.addAll(SMB2FileProvider.capabilities);
    }

    @Override
    protected FileObject createFile(AbstractFileName name) throws Exception {
        DiskEntry file = client.openFile(name.toString(), EnumSet.of(GENERIC_ALL), EnumSet.of(FILE_ATTRIBUTE_NORMAL),
                SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, EnumSet.of(FILE_DIRECTORY_FILE));
        SMB2FileObject smb2FileObject = new SMB2FileObject(file, this);
        return smb2FileObject;
    }

    protected String getPath() {return client.getSmbPath().getPath();}
    protected String getSharename() {return client.getSmbPath().getShareName();}
    protected String getHostname() {return client.getSmbPath().getHostname();}
}
