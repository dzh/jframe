/**
 * 
 */
package jframe.azure.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Before;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

/**
 * @author dzh
 * @date Jun 15, 2016 2:53:42 PM
 * @since 1.0
 */
public class TestBlobService {

    public static final String storageConnectionString = "DefaultEndpointsProtocol=http;"
            + "AccountName=your_storage_account;" + "AccountKey=your_storage_account_key;"
            + "EndpointSuffix=core.chinacloudapi.cn";

    // String storageConnectionString =
    // RoleEnvironment.getConfigurationSettings().get("StorageConnectionString");

    private CloudBlobContainer container;

    @Before
    public void testCreateContainer() {
        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Get a reference to a container.
            // The container name must be lower case
            container = blobClient.getContainerReference("mycontainer");

            // Create the container if it does not exist.
            container.createIfNotExists();
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public void testContainerPermission() {
        // Create a permissions object.
        BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

        // Include public access in the permissions object.
        containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

        // Set the permissions on the container.
        try {
            container.uploadPermissions(containerPermissions);
        } catch (StorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testUploadBlob() {
        try {
            // Define the path to a local file.
            final String filePath = "C:\\myimages\\myimage.jpg";

            // Create or overwrite the "myimage.jpg" blob with contents from a
            // local file.
            CloudBlockBlob blob = container.getBlockBlobReference("myimage.jpg");
            File source = new File(filePath);
            blob.upload(new FileInputStream(source), source.length());
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public void testListBlob() {
        try {
            // Loop over blobs within the container and output the URI to each
            // of them.
            for (ListBlobItem blobItem : container.listBlobs()) {
                System.out.println(blobItem.getUri());
            }
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public void testDownloadBlob() {
        try {
            // Loop through each blob item in the container.
            for (ListBlobItem blobItem : container.listBlobs()) {
                // If the item is a blob, not a virtual directory.
                if (blobItem instanceof CloudBlob) {
                    // Download the item and save it to a file with the same
                    // name.
                    CloudBlob blob = (CloudBlob) blobItem;
                    blob.download(new FileOutputStream("C:\\mydownloads\\" + blob.getName()));
                }
            }
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public void deleteBlob() {
        try {
            // Retrieve reference to a blob named "myimage.jpg".
            CloudBlockBlob blob = container.getBlockBlobReference("myimage.jpg");

            // Delete the blob.
            blob.deleteIfExists();
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public void deleteBlobContainer() {
        try {
            // Delete the blob container.
            container.deleteIfExists();
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

}
