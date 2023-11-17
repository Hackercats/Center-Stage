package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.util.AutoToTele;

import java.util.Random;

public class AutoConstants1 {
    SampleMecanumDrive drive;
    int randomMessageIndex;
    // Constructor
    public AutoConstants1(SampleMecanumDrive drive){
        this.drive = drive;
        randomMessageIndex = new Random().nextInt(messageList.length);
    }

    // 1 is red, -1 is blue
    private int alliance = 1;
    public int getAlliance() {return alliance;}
    public void setAlliance(int alliance) {this.alliance = alliance;}

    public boolean allianceToBool(){
        return alliance == 1;
    }
    public String allianceToString(){
        if (alliance == 1) return "Red Alliance";
        else return "Blue Alliance";
    }

    private int zone = 1;
    public int getDropLocation() {
        return zone;
    }
    // This is used so we don't put our pixel in the same slot as our partner. Hopefully. We'll see.
    int dropOffset = 0;

    boolean WingSide = false;
    public boolean isWingSide() {
        return WingSide;
    }
    public void setWingSide(boolean wingSide) {
        WingSide = wingSide;
    }

    private int numCycles = 4;
    public int getNumCycles() {return numCycles;}
    public void setNumCycles(int numCycles) {this.numCycles = numCycles;}

    private int delaySeconds = 0; // To be used to avoid collisions
    public int getDelaySeconds(){return delaySeconds;}
    public void setDelaySeconds(int seconds){
        delaySeconds = seconds;
    }


    public void updateDropLocationFromVisionResult(int visionResult){
        switch(visionResult){
            case 0:
                // Switch 1 and 3 if we're on blue terminal
                if (getAlliance() == 1){
                    zone = 1;
                } else {
                    zone = 3;
                }
                break;
            case 1:
                // We don't have to change the middle positon however
                zone = 2;
                break;
            case 3:
                // Switch 3 and 1 if we're on blue terminal
                if (getAlliance() == 1) {
                    zone = 3;
                } else {
                    zone = 1;
                }
                break;
        }
    }

    // Pose2d's
    public Pose2d startPos = new Pose2d(-35.8, -63* alliance, Math.toRadians(-90* alliance));

    // Set parkPos to a default to avoid null issues
    public Pose2d dropPos = new Pose2d(-57, -12* alliance, Math.toRadians(180* alliance));

    public Pose2d[] dropPositions;

    public void updateScoringPositions(int posIndex){
        dropPositions = new Pose2d[]{
                // Pos 1
                new Pose2d(54, -27 * alliance, Math.toRadians(0 * alliance)),
                // Pos 2
                new Pose2d(54, -34 * alliance, Math.toRadians(0 * alliance)),
                // Pos 3
                new Pose2d(54, -40 * alliance, Math.toRadians(0 * alliance))
        };
        dropPos = dropPositions[posIndex-1]; // Ahh so that's why I used 0-2 instead of 1-3
    }

    public TrajectorySequence dropOffPurplePixel;
    public TrajectorySequence throughBridge;
    public TrajectorySequence scoreYellowPixel;
    public TrajectorySequence park;

    public void updateTrajectories(int spikeMarkIndex) {

        if (isWingSide()) {
            startPos = new Pose2d(-35.25, -63 * alliance, Math.toRadians(-90 * alliance));
        } else {
            startPos = new Pose2d(11.75, -63 * alliance, Math.toRadians(-90 * alliance));
        }

        // Ahhhh we have to have six unique purple pixel trajectories
        if (isWingSide()){
            switch (spikeMarkIndex){
                case 1:
                    dropOffPurplePixel = drive.trajectorySequenceBuilder(startPos)
                            .splineToSplineHeading(new Pose2d(-61, -11 * alliance, Math.toRadians(-90 * alliance)), Math.toRadians(-90))
                            .build();
                case 2:
                    dropOffPurplePixel = drive.trajectorySequenceBuilder(startPos)
                            .splineToSplineHeading(new Pose2d(-37, -11 * alliance, Math.toRadians(-90 * alliance)), Math.toRadians(-90))
                            .build();
                default:
                    dropOffPurplePixel = drive.trajectorySequenceBuilder(startPos)
                            .splineToSplineHeading(new Pose2d(-13, -11 * alliance, Math.toRadians(-90 * alliance)), Math.toRadians(-90))
                            .build();
            }
        } else {
            switch (spikeMarkIndex){
                case 1:
                    dropOffPurplePixel = drive.trajectorySequenceBuilder(startPos)
                            .splineToSplineHeading(new Pose2d(1, -11 * alliance, Math.toRadians(-90 * alliance)), Math.toRadians(-90))
                            .build();
                case 2:
                    dropOffPurplePixel = drive.trajectorySequenceBuilder(startPos)
                            .splineToSplineHeading(new Pose2d(11.75, -11 * alliance, Math.toRadians(-90 * alliance)), Math.toRadians(-90))
                            .build();
                default:
                    dropOffPurplePixel = drive.trajectorySequenceBuilder(startPos)
                            .splineToSplineHeading(new Pose2d(22, -11 * alliance, Math.toRadians(-90 * alliance)), Math.toRadians(-90))
                            .build();
            }
        }

        if (isWingSide()){
            throughBridge = drive.trajectorySequenceBuilder(dropOffPurplePixel.end())

                    .build();
            park = drive.trajectorySequenceBuilder(scoreYellowPixel.end())
                    .setTangent(Math.toRadians(-130 * alliance))
                    .splineToSplineHeading(new Pose2d(-58, -12.2 * alliance, Math.toRadians(180 * alliance)), Math.toRadians(180 * alliance))
                    .lineTo(new Vector2d(-64.8, -12.2 * alliance))
                    .build();
        } else {
            throughBridge = drive.trajectorySequenceBuilder(dropOffPurplePixel.end())

                    .build();
            park = drive.trajectorySequenceBuilder(scoreYellowPixel.end())
                    .setTangent(Math.toRadians(-130 * alliance))
                    .splineToSplineHeading(new Pose2d(-58, -12.2 * alliance, Math.toRadians(180 * alliance)), Math.toRadians(180 * alliance))
                    .lineTo(new Vector2d(-64.8, -12.2 * alliance))
                    .build();
        }

        scoreYellowPixel = drive.trajectorySequenceBuilder(throughBridge.end())
                .build();
    }

    public void saveAutoPose(){
        AutoToTele.endOfAutoPose = drive.getPoseEstimate();
        AutoToTele.endOfAutoHeading = drive.getPoseEstimate().getHeading();
    }

    // Telemetry stuff
    public void addTelemetry(Telemetry telemetry){
        // Write the alliance in its color
        telemetry.addLine("<font color =#"+ (allianceToBool() ? "ff0000>" : "0000ff>") + allianceToString() + "</font>" );
        telemetry.addData("Alliance side as integer", getAlliance());
        telemetry.addData("Side", (isWingSide() ? "Wing" : "Board")); // First time using this funny switchy thing
        telemetry.addData("Number of cycles", getNumCycles());
        telemetry.addData("Delay in seconds", delaySeconds);
        telemetry.addData("Zone", zone);

        telemetry.addLine(ramdomAutoCheckMessage());
    }
    String[] messageList = {
            "CHECK THE AUTO, REMEMBER NANO FINALS 3!",
            "Ok apparently it was Nano Finals 2",
            "Run the right auto kids!",
            "Is it red? is it blue?",
            "Is it left? is it right?",
            "Are you SURE this is the program you want?",
            "Ejecute el auto correcto!",
            "올바른 자동 실행",
            "Oi mate, didjya checkit eh?",
            "What do those numbers say, hmmmm? Hmmmm?",
            "C'mon man, just take a second to read the stuff",
            "运行正确的自动",
            "Don't waste the potential of this bot",
            "How many cycles are we doin'?",
            "Where are we parkin'?",
            "Look. At. The. Side.",
            "Look at the bot, now look at the screen",
            "(insert mildly funny comment about auto)",
            "ELYYYYY...",
            "LUUUKEE...",
            ":)",
            "Don't lose worlds!",
            "Pay attention bro",
            "Don't pull a brainstormers FF finals",
            "Guys this auto's more complicated than last year",
            "Is it wing? is it board?"
    };
    String ramdomAutoCheckMessage(){
        //look up the index of the randomly generated number in the array of messages and return that message
        return messageList[randomMessageIndex];
    }
}