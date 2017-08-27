package sshserver;

import java.awt.SystemTray;
import java.io.File;
import java.util.Collections;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

public class SSHServer {

	public static final String APP_NAME="SSH Server";
	public static final String APP_VERSION="v001";
	public static final File WorkingDir=new File(System.getProperty("user.home")+File.separator+".sshserver");
	public static final File HostKeyFile=new File(WorkingDir+File.separator+"host.key");
	public static TaskbarIcon trayIcon;
	public static SshServer sshd;
	
	public static void startSSHServer() throws Exception {
		sshd.setPort(Configuration.getPort());
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(HostKeyFile.toPath()));
        sshd.setShellFactory(InteractiveProcessShellFactory.INSTANCE);
	    sshd.setPasswordAuthenticator((un, pw, session) -> Configuration.getUsername().equals(un) && Configuration.getPassword().equals(pw));
	    sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
	    sshd.setTcpipForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
	    sshd.setCommandFactory(new ScpCommandFactory() {
	        public Command createCommand(String command) {
	        	System.out.println(command);
	            return null;
	        }
	    });
	    sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
	    sshd.start();
	    
	    if (trayIcon!=null) {
	    	trayIcon.setToolTip(SSHServer.APP_NAME+" is running on port "+Configuration.getPort()+".");
	    	trayIcon.startServerMenu.setEnabled(false);
	    	trayIcon.stopServerMenu.setEnabled(true);
	    }
	}
	
	public static void stopSSHServer() throws Exception {
	    if (trayIcon!=null) trayIcon.setToolTip(SSHServer.APP_NAME+" is being stopped.");
		Thread t=new Thread(() -> {
			try { sshd.close(); } catch (Exception zzz) {}
		});
		t.start();
		
		while (!sshd.isClosed()) Thread.sleep(100);
	    if (trayIcon!=null) {
	    	trayIcon.setToolTip(SSHServer.APP_NAME+" has stopped.");
	    	trayIcon.startServerMenu.setEnabled(true);
	    	trayIcon.stopServerMenu.setEnabled(false);
	    }
	}
	
	public static void createSystemTray() {
		if (!SystemTray.isSupported()) return;
		
		trayIcon=new TaskbarIcon(Utility.getAppIconSmall().getImage());
		
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (Exception e) {}
	}
	
	public static void main (String [] args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		if (!WorkingDir.exists()) WorkingDir.mkdirs();
		sshd=SshServer.setUpDefaultServer();
		Configuration.loadSettings();
		
		createSystemTray();
        try {
    		startSSHServer();
        } catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to start SSH server. "+e.getMessage(), "SSH Server", JOptionPane.ERROR_MESSAGE);
        }
        
        Thread.sleep(Long.MAX_VALUE);
	}
}
