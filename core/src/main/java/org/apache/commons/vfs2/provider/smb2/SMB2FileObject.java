package org.apache.commons.vfs2.provider.smb2;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.protocol.transport.TransportException;
import com.hierynomus.smbj.share.Directory;
import com.hierynomus.smbj.share.DiskEntry;
import com.hierynomus.smbj.share.File;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SMB2FileObject extends AbstractFileObject<SMB2FileSystem> {
    private DiskEntry file;
    private SMB2FileSystem fileSystem;
    private SMB2FileName fileName;

    protected SMB2FileObject(final DiskEntry file,  final SMB2FileSystem fileSystem) throws TransportException {
        super(new SMB2FileName(fileSystem.getHostname(), fileSystem.getSharename(), file.getFileInformation().getNameInformation(), file.getFileInformation().getStandardInformation().isDirectory()? FileType.FOLDER:FileType.FILE), fileSystem);
        this.file = file;
        this.fileName = new SMB2FileName(fileSystem.getHostname(), fileSystem.getSharename(), file.getFileInformation().getNameInformation(), file.getFileInformation().getStandardInformation().isDirectory()? FileType.FOLDER:FileType.FILE);
    }

    @Override
    protected long doGetContentSize() throws Exception {
        return file.getFileInformation().getStandardInformation().getAllocationSize();
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {
        return ((File)file).getInputStream();
    }

    @Override
    protected FileType doGetType() throws Exception {
        if(file.getFileInformation().getStandardInformation().isDirectory()) {
            return FileType.FOLDER;
        } else {
            return FileType.FILE;
        }
    }

    @Override
    protected String[] doListChildren() throws Exception {
        ArrayList<String> retVal = new ArrayList<String>();
        if(file.getFileInformation().getStandardInformation().isDirectory()) {
            Iterator<FileIdBothDirectoryInformation> list = ((Directory) file).iterator();
            while(list.hasNext()) {
                FileIdBothDirectoryInformation current = list.next();
                retVal.add(current.getFileName());
            }

        } else {
            throw new FileSystemException("Files have no children");
        }
        return (String[])retVal.toArray();
    }
}
