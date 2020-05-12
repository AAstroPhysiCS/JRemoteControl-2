package Server.Overlay.Node;

import java.io.Serializable;

public record OSInfoNode(String osName, String osVersion, String osVendor, String osArchitecture, String userAccName) implements Serializable {
    public String toString(){
        return """
                OS Name: %s
                OS Version: %s
                OS Vendor: %s
                OS Architecture: %s
                OS User Account Name: %s
                """.formatted(osName, osVersion, osVendor, osArchitecture, userAccName);
    }
}
