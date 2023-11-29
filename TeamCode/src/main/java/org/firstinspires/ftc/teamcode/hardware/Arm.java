package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Utility;

@Config
public class Arm {
    ServoImplEx pivot;
    Servo bottomPixel;
    Servo topPixel;
    Servo stopper;
    ColorSensor bottomPixelSensor;
    ColorSensor topPixelSensor;
    Rev2mDistanceSensor boardSensor;

    boolean bottomState = false; // True is closed, false open
    boolean topState = false;

    // Constants
    public static double pivotIntakingPos = 0.025;
    public static double pivotScoringPos = 0.865;
    public static double pivotPremovePos = 0.3;
    public static double pivotActuationTime = 300;
    public static double pivotAwayFromBordTime = 200;

    public static double gripperClosedPos = 0.88;
    public static double gripperOpenPos = 0.6;
    public static double gripperPosOffset = -0.05;
    public static double gripperActuationTime = 250; // In milliseconds

    public static double stopperClosedPos = 0.13;
    public static double stopperOpenPos = 0.9;
    boolean stopperState = false;

    public static double sensorThreshold = 3000;

    public Arm(HardwareMap hwmap){
        // Hardwaremap stuff
        pivot = hwmap.get(ServoImplEx.class, "pivot");
        pivot.setDirection(Servo.Direction.REVERSE);
        pivot.setPwmRange(new PwmControl.PwmRange(500,2500));
        bottomPixel = hwmap.get(Servo.class, "bottomPixel");
        topPixel = hwmap.get(Servo.class, "topPixel");
        bottomPixelSensor = hwmap.get(ColorSensor.class, "bottomSensor");
        topPixelSensor = hwmap.get(ColorSensor.class, "topSensor");
        boardSensor = hwmap.get(Rev2mDistanceSensor.class, "boardSensor");
        stopper = hwmap.get(Servo.class, "stopper");

        // Warning: Robot moves on intitialization
        pivotGoToIntake();
        setBothGrippersState(false);
        setStopperState(true);
    }

    // Control each
    public void setBottomGripperState(boolean state){
        bottomState = state;
        if (state) bottomPixel.setPosition(gripperClosedPos + gripperPosOffset);
        else bottomPixel.setPosition(gripperOpenPos + gripperPosOffset);
    }
    public boolean getBottomGripperState(){
        return bottomState;
    }

    public void setTopGripperState(boolean state){
        topState = state;
        if (state) topPixel.setPosition(gripperClosedPos);
        else topPixel.setPosition(gripperOpenPos);
    }
    public boolean getTopGripperState(){
        return topState;
    }

    public void setBothGrippersState(boolean state){
        setBottomGripperState(state);
        setTopGripperState(state);
    }
    public boolean getBothGrippersState(){
        // Returns true only if both are closed
        return (getBottomGripperState() && getTopGripperState());
    }

    public void setPivotPos(double pos){
        // Make sure it's a safe move
        double finalPos = Utility.clipValue(pivotIntakingPos, pivotScoringPos, pos);
        pivot.setPosition(finalPos);
    }
    public double getPivotPos(){
        // Take the pos of the one we didn't offset
        return pivot.getPosition();
    }
    public void pivotGoToIntake(){
        setPivotPos(pivotIntakingPos);
    }
    public void pivotScore(){
        setPivotPos(pivotScoringPos);
    }

    public void preMove(){
        setPivotPos(pivotPremovePos);
    }

    public void setStopperState(boolean state){
        // True for closed, false for open
        if (state) stopper.setPosition(stopperClosedPos);
        else stopper.setPosition(stopperOpenPos);
        stopperState = state;
    }
    public boolean getStopperState() {return stopperState;}

    public boolean pixelIsInBottom(){
        return bottomPixelSensor.alpha() > sensorThreshold;
    }
    public boolean pixelIsInTop(){
        return topPixelSensor.alpha() > sensorThreshold;
    }

    public double getBoardDistance(){
        return boardSensor.getDistance(DistanceUnit.CM);
    }

    public void displayDebug(Telemetry telemetry){
        telemetry.addLine("ARM");
        telemetry.addData("Pivot pos", pivot.getPosition());
        telemetry.addData("Bottom pos", bottomPixel.getPosition());
        telemetry.addData("Bottom state", getBottomGripperState());
        telemetry.addData("Bottom sensor val", bottomPixelSensor.alpha());
        telemetry.addData("Pixel in bottom", pixelIsInBottom());
        telemetry.addData("Top pos", topPixel.getPosition());
        telemetry.addData("Top state", getTopGripperState());
        telemetry.addData("Top sensor val", topPixelSensor.alpha());
        telemetry.addData("Pixel in top", pixelIsInTop());
        telemetry.addData("Stopper state", getStopperState());
        telemetry.addData("Board distance", getBoardDistance());
    }
}
