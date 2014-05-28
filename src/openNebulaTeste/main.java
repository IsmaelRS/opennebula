package openNebulaTeste;

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.apache.xmlrpc.client.XmlRpcClientConfig;
import org.apache.xmlrpc.XmlRpcRequestConfig;
import org.apache.xmlrpc.XmlRpcRequest;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client oneClient;
		
		try{
			System.out.println("bleh");
			oneClient = new Client("wanrly:12345", "http://cloud.quixada.ufc.br:5678");
			System.out.println("deu certo??");
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("ERRO!!/t/tbleh");
		}
		
	}

	public void teste() {
		// First of all, a Client object has to be created.
		// Here the client will try to connect to OpenNebula using the default
		// options: the auth. file will be assumed to be at $ONE_AUTH, and the
		// endpoint will be set to the environment variable $ONE_XMLRPC.
		Client oneClient;

		try {
			oneClient = new Client();

			// This VM template is a valid one, but it will probably fail to run
			// if we try to deploy it; the path for the image is unlikely to
			// exist.
			String vmTemplate = "NAME     = vm_from_java    CPU = 0.1    MEMORY = 64\n"
					+ "DISK     = [\n"
					+ "\tsource   = \"/home/user/vmachines/ttylinux/ttylinux.img\",\n"
					+ "\ttarget   = \"hda\",\n"
					+ "\treadonly = \"no\" ]\n"
					+ "FEATURES = [ acpi=\"no\" ]";

			System.out.print("Trying to allocate the virtual machine... ");
			OneResponse rc = VirtualMachine.allocate(oneClient, vmTemplate);

			if (rc.isError()) {
				System.out.println("failed!");
				throw new Exception(rc.getErrorMessage());
			}

			// The response message is the new VM's ID
			int newVMID = Integer.parseInt(rc.getMessage());
			System.out.println("ok, ID " + newVMID + ".");

			// We can create a representation for the new VM, using the returned
			// VM-ID
			VirtualMachine vm = new VirtualMachine(newVMID, oneClient);

			// Let's hold the VM, so the scheduler won't try to deploy it
			System.out.print("Trying to hold the new VM... ");
			rc = vm.hold();

			if (rc.isError()) {
				System.out.println("failed!");
				throw new Exception(rc.getErrorMessage());
			}

			// And now we can request its information.
			rc = vm.info();

			if (rc.isError())
				throw new Exception(rc.getErrorMessage());

			System.out.println();
			System.out
					.println("This is the information OpenNebula stores for the new VM:");
			System.out.println(rc.getMessage() + "\n");

			// This VirtualMachine object has some helpers, so we can access its
			// attributes easily (remember to load the data first using the info
			// method).
			System.out.println("The new VM " + vm.getName() + " has status: "
					+ vm.status());

			// And we can also use xpath expressions
			System.out.println("The path of the disk is");
			System.out.println("\t" + vm.xpath("template/disk/source"));

			// We have also some useful helpers for the actions you can perform
			// on a virtual machine, like cancel or finalize:

			rc = vm.finalizeVM();
			System.out.println("\nTrying to finalize (delete) the VM "
					+ vm.getId() + "...");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
