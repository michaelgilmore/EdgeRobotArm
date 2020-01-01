
NUI Chapter 6. Controlling a Robot Arm

From the website:

  Killer Game Programming in Java
  http://fivedots.coe.psu.ac.th/~ad/jg

  Dr. Andrew Davison
  Dept. of Computer Engineering
  Prince of Songkla University
  Hat yai, Songkhla 90112, Thailand
  E-mail: ad@fivedots.coe.psu.ac.th


If you use this code, please mention my name, and include a link
to the website.

Thanks,
  Andrew 10th July 2011

================================================================================
Last updated by by Craig Turner: 21st Jan 2013
 
If you use any of Craig's code please mention his name
and the Blog link:

http://gampageek.blogspot.co.uk/2013/01/java-working-beautifully-many-thanks-dr.html 

================================================================================
Contains Java files:
  * Joint.java, ArmCommunicator.java,
    JointID.java

Java files added by Craig Turner 19/01/2013
    * RobotUserForm.java (GUI interface)
    * ArmDance.java (sequence of moves a "dance")




You need to download and install:

   * libusb-win32:   libusb-win32-bin-1.2.6.0 
<http://sourceforge.net/projects/libusb-win32/files/libusb-win32-releases/>
        
  Extract archive and run the \bin\inf-wizard application. Choose a device to create
drivers for e.g. 0x1267 0x0000 ELAN USB Device
             
             --> copy and rename the relevant DLLs in e.g. bin\x86\.. over to 
the OS (x86 is windows example)

                   e.g. libusb0_x86.dll --> Windows\system32\libusb0.dll 
                        libusb0.sys --> Windows\systems32\drivers\libusb0.sys

   * libusbjava: <http://sourceforge.net/projects/libusbjava/>

<http://en.sourceforge.jp/projects/sfnet_libusbjava/downloads/libusbjava-snapshots/20090517/LibusbJava_dll_0.2.4.0.zip/>

             I downloaded ch.ntb.usb-0.5.9.jar and LibusbJava_dll_0.2.4.0.zip
             --> move the JAR and unzipped DLL to your java project folder

----------------------------
Other requirements:

  * An OWI-535 robot arm 
   (http://www.owirobot.com/products/Robotic-Arm-Edge.html), 
    **and** its USB interface.

In UK available here:
<http://www.rapidonline.com/Education/Robot-Arm-with-USB-PC-Interface-06-9343/?sid=4a3f2ea6-bc06-4375-8f77-0eb0e41a1491>
 

  * The driver software may not include a Windows 7 version, but the 
    OWI Robotics website has drivers at 
      http://www.owirobot.com/pages/Downloads.html

  * install the arm's ELAN USB driver

  * Create a libusb-win32 device driver for the ELAN driver using 
    libusb-win32's inf-wizard.exe
------------------------------------------------------------------------------
 ArmDance.java
^^^^^^^^^^^^^^^^^
 * To use this class and it's methods you need to set the arm to a suitable position
 * so that  when the sequences begin the arm doesn't overshoot it's working 
 * limits. Failure to do so may cause the gears to grind and click. The bot 
 * may hit things, fall over and generally cause havoc
 * 
 * For  dance1(1, 5, 2);
        faceCamShowOff();
 * 
 * I used the GUI (RobotUserForm.java) to set the arm to fully vertical
 * before starting.

-----------------------------------------------------------------------------
Last updated by by Craig Turner: 21st Jan 2013
 
If you use any of Craig's code please mention his name
and the Blog :
http://gampageek.blogspot.co.uk/2013/01/java-working-beautifully-many-thanks-dr.html 