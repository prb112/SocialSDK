/*
 * © Copyright IBM Corp. 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.ibm.sbt.services.client.connections.files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import com.ibm.sbt.services.BaseUnitTest;
import com.ibm.sbt.services.client.ClientServicesException;
import com.ibm.sbt.services.client.base.datahandlers.EntityList;
import com.ibm.sbt.services.client.connections.communities.Community;
import com.ibm.sbt.services.client.connections.communities.CommunityService;
import com.ibm.sbt.services.client.connections.files.model.FileRequestParams;
import com.ibm.sbt.test.lib.TestEnvironment;

public class FileServiceTest extends BaseUnitTest {

    public final static String TEST_CONTENT = "This is a sample Content in the Test File. "
                                                    + "Used mainly for Testing the Upload functionality of the FileService Connections API."
                                                    + "Test Input : ddsfafw4t547���%*���^U���^JUL&><\03242";
    public final static String TEST_NAME    = "FS_TestUploadTest";

    @Test
    public void testReadFile() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String testFileId = listOfFiles.get(0).getFileId();
        File entry = fileService.getFile(testFileId, true);
        assertEquals(entry.getCategory(), "document");
        assertEquals(entry.getFileId(), testFileId);
    }

    @Test
    public void testReadFileWithLoadFalse() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String testFileId = listOfFiles.get(0).getFileId();
        File entry = fileService.getFile(testFileId, false);
        assertNull(entry.getCategory());
    }

    @Test
    public void testGetMyFiles() throws Exception {
        FileService fileService = new FileService();
        List<File> fileEntries = fileService.getMyFiles();
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "document");
                assertEquals(fEntry.getAuthor().getName(), TestEnvironment.getCurrentUserDisplayName());
            }
        }
    }

    @Test
    public void testGetFilesSharedWithMe() throws Exception {
        FileService fileService = new FileService();
        List<File> fileEntries = fileService.getFilesSharedWithMe();
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertNotNull(fEntry.getVisibility());
            }
        }
    }

    @Test
    public void testGetFilesSharedByMe() throws Exception {
        FileService fileService = new FileService();
        List<File> fileEntries = fileService.getFilesSharedByMe();
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "document");
                // assertEquals(fEntry.getVisibility(), "shared");
            }
        }
    }

    @Test
    public void testGetPublicFiles() throws Exception {
        //TODO: fix for smartcloud
        FileService fileService = new FileService();
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        List<File> fileEntries = fileService.getPublicFiles(null);
        assertNotNull(fileEntries);
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "document");
            }
        }

    }

    @Test
    public void testGetPinnedFiles() throws Exception {
        FileService fileService = new FileService();
        List<File> fileEntries = fileService.getPinnedFiles(null);
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "document");
            }
        }
    }

    @Test
    public void testGetMyFolders() throws Exception {
        //TODO API BROKEN
        FileService fileService = new FileService();
        //List<File> fileEntries = fileService.getMyFolders(null);
        //if (fileEntries != null && !fileEntries.isEmpty()) {
        //		for (File fEntry : fileEntries) {
        //			assertEquals(fEntry.getCategory(), "collection");
        //		}
        //	}
    }

    @Test
    public void pinAndUnpinFolder() throws Exception {
        //TODO: fix for smartcloud
        FileService fileService = new FileService();
        if (TestEnvironment.isSmartCloudEnvironment())
            return;

        //Pin the first folder in My Folders
        List<File> fileEntries = fileService.getPublicFolders(null);
        fileService.pinFolder(fileEntries.get(0).getFileId());

        //Check that the folder is now in the list of Pinned Folders and the list is > 0
        List<File> pinnedFolders = fileService.getPinnedFolders();
        assertTrue(pinnedFolders.size() > 0);
        assertEquals(fileEntries.get(0).getFileId(), pinnedFolders.get(0).getFileId());

        //Remove the Pinned folder and check that pinned folders no longer contains the folder
        fileService.unPinFolder(pinnedFolders.get(0).getFileId());
        if (pinnedFolders.size() > 0) {
            assertTrue(fileEntries.get(0).getFileId() != pinnedFolders.get(0).getFileId());
        }
    }

    @Test
    public void testGetPinnedFolders() throws Exception {
        FileService fileService = new FileService();
        List<File> fileEntries = fileService.getPinnedFolders();
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "collection");
            }
        }
    }

    @Test
    public void testGetFoldersWithRecentlyAddedFiles() throws Exception {
        FileService fileService = new FileService();
        List<File> fileEntries = fileService.getFoldersWithRecentlyAddedFiles(null);
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "collection");
            }
        }
    }

    @Test
    public void testGetFilesInFolder() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        EntityList<File> listOfFolders = fileService.getPublicFolders();
        String testFolderId = listOfFolders.get(0).getFileId();
        List<File> fileEntries = fileService.getFilesInFolder(testFolderId, null);
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "document");
            }
        }
    }

    @Test
    public void testGetAllUserFiles() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        List<File> fileEntries = fileService.getSharedUserFiles(TestEnvironment.getSecondaryUserUuid());
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "document");
            }
        }
    }

    @Test
    public void testGetAllFileComments() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> files = fileService.getMyFiles();
        String fileId = files.get(0).getFileId();
        EntityList<Comment> commentEntries = fileService.getMyFileComments(fileId, null);
        if (commentEntries != null && !commentEntries.isEmpty()) {
            for (Comment fEntry : commentEntries) {
                assertNotNull(fEntry.getComment());
            }
        }
    }

    @Test
    public void testGetFilesComments() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        EntityList<File> files = fileService.getPublicFiles();
        assertNotNull(files);
        File file = files.get(0);
        if (null == file) {
            System.err.println("There are no public files on the test server, please populate some");
            return;
        }
        String fileId = file.getFileId();
        EntityList<Comment> commentEntries = fileService.getAllUserFileComments(fileId,
                TestEnvironment.getCurrentUserUuid());
        if (!commentEntries.isEmpty()) {
            if (commentEntries != null && !commentEntries.isEmpty()) {
                for (Comment fEntry : commentEntries) {
                    assertNotNull(fEntry.getComment());
                }
            }
        }
    }

    @Test
    public void testGetFilesInMyRecycleBin() throws Exception {
        FileService fileService = new FileService();
        List<File> fileEntries = fileService.getFilesInMyRecycleBin(null);
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (File fEntry : fileEntries) {
                assertEquals(fEntry.getCategory(), "document");
            }
        }
    }

    @Test
    public void testUpdateFileMetadata() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String testFileId = listOfFiles.get(0).getFileId();
        File fileEntry = fileService.getFile(testFileId, false);
        Map<String, String> paramsMap = new HashMap<String, String>();
        Random random = new Random();
        paramsMap.put(FileRequestParams.TAG.getFileRequestParams(), "Junit_Tag" + random.nextInt());
        String label = "Junit_Label_New" + random.nextInt();
        fileEntry.setLabel(label);
        fileEntry = fileService.updateFileMetadata(fileEntry, paramsMap);
        assertEquals(unRandomize(fileEntry.getLabel()), unRandomize(label));
    }

    @Test
    public void testLock() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        File file = listOfFiles.get(0);
        if (file.isLocked()) {
            file.unlock();
            //assertTrue(!file.isLocked());  //TODO MAKE LOCK AND UNLOCK MODIFY THE LOCKED STATE ON THE FILE OBJECT SO THAT islocked IS ACCURATE AFTER LOCKING/UNLOCKING
        }
        String testFileId = file.getFileId();
        fileService.lock(testFileId);
        File fileEntry = fileService.getFile(testFileId, true);
        assertEquals("HARD", fileEntry.getLockType());
    }

    @Test
    public void testUnlock() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        File file = listOfFiles.get(0);
        if (!file.isLocked()) {
            file.lock();
            //assertTrue(file.isLocked()); //TODO MAKE LOCK AND UNLOCK MODIFY THE LOCKED STATE ON THE FILE OBJECT SO THAT islocked IS ACCURATE AFTER LOCKING/UNLOCKING
        }
        String testFileId = file.getFileId();
        fileService.unlock(testFileId);
        File fileEntry = fileService.getFile(testFileId, true);
        assertEquals("NONE", fileEntry.getLockType());
    }

    @Test
    public void testPinAndUnPin() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String testFileId = listOfFiles.get(0).getFileId();

        fileService.pinFile(testFileId);
        List<File> pinnedFiles = fileService.getPinnedFiles(null);
        assertTrue(pinnedFiles.size() > 0);
        assertEquals(testFileId, pinnedFiles.get(0).getFileId());

        fileService.unPinFile(testFileId);
        pinnedFiles = fileService.getPinnedFiles(null);
        if (pinnedFiles.size() > 0) {
            assertTrue(testFileId != pinnedFiles.get(0).getFileId());
        }

    }

    @Test
    public void testDelete() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String testDeleteFileId = listOfFiles.get(0).getFileId();
        fileService.deleteFile(testDeleteFileId);
    }

    @Test
    public void testAddCommentToFile() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String testFileId = listOfFiles.get(0).getFileId();
        File fileEntry = fileService.getFile(testFileId, true);
        String comment = "Junit Comment - Added from FileServiceTest, testAddCommentToFile";
        Comment commentEntry;
        commentEntry = fileService.addCommentToFile(fileEntry.getFileId(), comment, fileEntry.getAuthor()
                .getId(), null);
        assertEquals(comment, commentEntry.getComment());
    }

    @Test
    public void testAddCommentToMyFile() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String testFileId = listOfFiles.get(0).getFileId();
        File fileEntry = fileService.getFile(testFileId, true);
        String comment = "Junit Comment - Added from FileServiceTest, testAddCommentToMyFile";
        Comment commentEntry = fileService.addCommentToFile(fileEntry.getFileId(), comment, null);
        assertEquals(comment, commentEntry.getComment());
    }

    @Test
    public void testFileUpload() throws IOException, ClientServicesException, Exception {
        java.io.File t = java.io.File.createTempFile(TEST_NAME, "txt");
        t.deleteOnExit();
        FileOutputStream s = new FileOutputStream(t);
        s.write(TEST_CONTENT.getBytes());
        s.flush();
        s.close();
        FileService service = new FileService();
        FileInputStream inputStream = new FileInputStream(t);
        File entry = service.uploadFile(inputStream, t.getName(), TEST_CONTENT.length());
        assertNotNull(entry.getCategory());
        service.deleteFile(entry.getFileId());
    }

    @Test
    public void testGetNonce() {
        FileService fileService = new FileService();
        String nonce = null;
        try {
            nonce = fileService.getNonce();
        } catch (ClientServicesException e) {

        }
        assertNotNull(nonce);
    }

    @Test
    public void testAddRemoveFileToFolders() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        EntityList<File> folders = fileService.getPublicFolders();
        List<String> listOfFolderIds = new ArrayList<String>();
        for (File folder : folders) {
            listOfFolderIds.add(folder.getFileId());
        }
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String fileId = listOfFiles.get(0).getFileId();
        fileService.addFileToFolders(fileId, listOfFolderIds);

        // now removing file from folders. 
        for (String folderId : listOfFolderIds) {
            fileService.removeFileFromFolder(folderId, fileId);
        }
    }

    @Ignore
    @Test
    public void testAddFilesToFolder() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        List<String> fs = new LinkedList<String>();
        fs.add("FileID1");
        fs.add("FileID2");
        //TODO: fix with getmy folder implementation
        fileService.addFilesToFolder("folderid", fs, null);
    }

    @Test
    public void testCreateComment() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String fileId = listOfFiles.get(0).getFileId();
        String comment = "TestCreateComment From FileServiceTest";
        Comment commentObject = fileService.createComment(fileId, comment);
        assertEquals(comment, commentObject.getComment());
    }

    @Test
    public void testCreateDeleteFolder() throws Exception {
        FileService fileService = new FileService();
        Random random = new Random(System.currentTimeMillis());
        String name = "testCreateFolder"  + random.nextInt();
        String description = "testCreateFolder" + random.nextInt();
        File folder = fileService.createFolder(name, description);
        assertEquals(unRandomize(folder.getTitle()), unRandomize(name));

        //now delete the folder created
        fileService.deleteFolder(folder.getFileId());
    }

    @Test
    public void testDeleteComment() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String fileId = listOfFiles.get(0).getFileId();
        EntityList<Comment> commentObject = fileService.getMyFileComments(fileId);
        if (!commentObject.isEmpty()) {
            String commentId = commentObject.get(0).getCommentId();
            fileService.deleteComment(fileId, commentId);
        }
    }

    @Test
    public void testShareFileWithCommunity() throws Exception {
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        String name = "test file " + System.currentTimeMillis();

        byte[] bytes = name.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        HashMap<String, String> p = new HashMap<String, String>();
        p.put("visibility", "public");
        File file = fileService.uploadFile(bais, name, bytes.length, p);
        
        String fileId = file.getFileId();
        
        EntityList<Community> publicCommunities = new CommunityService().getPublicCommunities();

        List<String> c = new LinkedList<String>();
        c.add(publicCommunities.get(0).getCommunityUuid());
        c.add(publicCommunities.get(1).getCommunityUuid());
        
        fileService.shareFileWithCommunities(fileId, c, null);
        
        fileService.deleteFile(file.getFileId());
    }

    @Test
    public void testDeleteFileFromRecycleBin() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getFilesInMyRecycleBin();
        String fileId = listOfFiles.get(0).getFileId();
        fileService.deleteFileFromRecycleBin(fileId);
    }

    @Test
    public void testGetFileShares() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getFileShares();
    }

    @Test
    public void testGetFolder() throws Exception {
        //TODO: fix for smartcloud
        if (TestEnvironment.isSmartCloudEnvironment())
            return;
        FileService fileService = new FileService();
        EntityList<File> folders = fileService.getPublicFolders();
        if (folders != null) {
            File folder = fileService.getFolder(folders.get(0).getFileId());
            assertNotNull(folder.getTitle());
        }
    }

    @Test
    public void testRestoreFileFromRecycleBin() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> files = fileService.getFilesInMyRecycleBin();
        if (files != null) {
            fileService.restoreFileFromRecycleBin(files.get(0).getFileId());
        }
    }

    @Test
    public void testUpdateComment() throws Exception {
        FileService fileService = new FileService();
        EntityList<File> listOfFiles = fileService.getMyFiles();
        String fileId = listOfFiles.get(0).getFileId();
        Comment commentObject = fileService.createComment(fileId,
                "CommentCreated" + System.currentTimeMillis());
        if (commentObject != null) {
            String commentId = commentObject.getCommentId();
            fileService.updateComment(fileId, commentId,
                    commentObject.getComment() + System.currentTimeMillis());
        }
    }

    @Test
    public void testUploadCommunityFile() throws Exception {

        CommunityService communityService = new CommunityService();
        EntityList<Community> communityList = communityService.getMyCommunities();
        String communityUuid = null;
        if (communityList.isEmpty()) {
            String type = TestEnvironment.isSmartCloudEnvironment() ? "private" : "public";
            communityUuid = communityService.createCommunity(
                    "UploadCommunityFile-" + System.currentTimeMillis(), "", type);
        } else {
            communityUuid = communityList.get(0).getCommunityUuid();
        }
        byte[] bytes = "HelloWord".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        FileService fileService = new FileService();
        fileService.uploadCommunityFile(bais, communityUuid, "HelloWord" + System.currentTimeMillis()
                + ".txt", bytes.length);

    }
}
