package OWI535RobotArm;

/**
 * Make the OWI-535 Robot Arm Dance by performing sequences of
 * movements - a demonstration
 *
 * @author Craig Turner
 * 
 * @see <http://gampageek.blogspot.co.uk/2013/01/blog-post.html>
 * /
 
 
 /* To use this class and it's methods you need to set the arm to a suitable position
 * so that  when the sequences begin the arm doesn't overshoot it's working 
 * limits. Failure to do so may cause the gears to grind and click. The bot 
 * may hit things, fall over and generally cause havoc
 * 
 * For  dance1(1, 5, 2);
        faceCamShowOff();
 * 
 * I used the GUI (RobotUserForm.java) to set the arm to fully vertical
 * before starting.
 */


public class ArmDance
{
    

    /**
     * @param args the command line arguments
     */
    
    // ArmCommunicator() command reference:
    /* 
     System.out.println("  Gripper: close == w ; open == s");
     System.out.println("  Wrist:     fwd == e ; back == d");
     System.out.println("  Elbow:     fwd == r ; back == f");
     System.out.println("  Shoulder:  fwd == u ; back == j");
     System.out.println("  Base:     left == k ; right == i");
     System.out.println("  Light:      on == l ; off == p");
     System.out.println("            quit == q ; help == ?");
     */
   
    public static void main(String args[]) // MAIN ****************************
    {

        //dance1(int danceMoves, int bendMoves, int twofroMoves)
        dance1(1, 5, 2);// do dance1 once
        faceCamShowOff();
        

    }

   /**
     *
     * @param moves
     * Number of increments to close and open gripper
     * 
     * @param times
     * Number of times to open and close gripper
     * 
     */
    
    private static void GripperFlex(int moves, int times)
    {
        for (int j = 0; j < times; j++)
        {
            for (int i = 0; i < moves ; i++)
            {
                ArmCommunicator.doArmOp('s', RobotUserForm.arm1);// open
            }
            
            Flashlight(2);
            
            for (int i = 0; i < moves; i++)
            {
                ArmCommunicator.doArmOp('w', RobotUserForm.arm1);// close

            }
        }
    }

    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    
    private static void WristForward(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('e', RobotUserForm.arm1);
        }
    }

    
    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    private static void WristBackward(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('d', RobotUserForm.arm1);
        }
    }

    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    private static void ShoulderForward(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('u', RobotUserForm.arm1);
        }
    }  

    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    private static void ShoulderBackward(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('j', RobotUserForm.arm1);
        }
    }  

    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    private static void ElbowForward(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('r', RobotUserForm.arm1);
        }
    }

    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    private static void ElbowBackward(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('f', RobotUserForm.arm1);
        }
    }

    /**
     *
     * @param bendMoves
     * Number of increments to bend elbow and shoulder
     * 
     *  
     */
    
    private static void BendForward(int bendMoves)// elbow and shoulder
    {

        for (int i = 0; i < bendMoves; i++)
        {
            ShoulderForward(1);
            ElbowForward(1);

        }
    }

    /**
     *
     * @param bendMoves
     * Number of increments to bend elbow and shoulder
     * 
     *  
     */
    
    
    private static void BendBackward(int bendMoves)// elbow and shoulder
    {

        for (int i = 0; i < bendMoves; i++)
        {
            ShoulderBackward(1);
            waveWrist(1);
            ElbowBackward(1);
        }
    }

   /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    private static void BaseRight(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('i', RobotUserForm.arm1);

        }
    }

    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */   
    
    private static void BaseLeft(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('k', RobotUserForm.arm1);

        }
    }

    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    private static void Flashlight(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            ArmCommunicator.doArmOp('l', RobotUserForm.arm1);// ON
            DelayTenthsOfSec(3); // pause for .3 sec
            ArmCommunicator.doArmOp('p', RobotUserForm.arm1);// OFF
            DelayTenthsOfSec(3); // pause for .3 sec
        }

    }

    /**
     *
     * @param moves
     * Number of increments to move 
     * 
     *  
     */
    
    private static void waveWrist(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            WristForward(8 + 1);
            WristBackward(8);
        }
    }

     /**
     *
     * @param moves
     * Number of times to move back and forth
     * 
     *  
     */
    
    private static void BaseTwofro(int moves)
    {

        for (int i = 0; i < moves; i++)
        {
            BaseLeft(8);
            BaseRight(16);
            BaseLeft(8);
        }
    }

     /**
     *
     * @param danceMoves
     * Number of times to do the dance 
     * 
     * @param bendMoves
     * Number of increments to bend elbow and shoulder in a sub-sequence
     * 
     * @param twofroMoves
     * Number of times to move back and forth in a sub-sequence
     * 
     */
    
    
    private static void dance1(int danceMoves, int bendMoves, int twofroMoves)
    {

        for (int i = 0; i < danceMoves; i++)
        {

            BendForward(bendMoves);
            BaseTwofro(twofroMoves);
            BendBackward(bendMoves + 1);


        }
    }

    
    
    private static void faceCamShowOff()
    {// face the camera and show off with special moves
        
        BaseRight(12);
        ElbowForward(3);
        WristForward(8);
                
        GripperFlex(8, 2);

        GripperFlex(8, 2);
        WristBackward(10);
        ElbowBackward(8);
        ElbowForward(10);
        waveWrist(3);
        
    }

    /**
     *
     * @param multi
     * multiples of 0.1 of a second to pause
     
     */
    
    private static void DelayTenthsOfSec(int multi) // delay multis of 1000ms (0.1 sec)
    {

        try
        {
            Thread.sleep(100 * multi); // sleep for now 100ms (0.1 sec) * multi

        } catch (InterruptedException ie)
        {
            System.out.println(ie.getMessage());
        }


    }
}// end of class ArmDance

