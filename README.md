# LCN for openHAB
This is an alternative binding for LCN for [openHAB](http://www.openhab.org/)! It was developed at the same time as the binding was offered from the manufacturer. You can find the "original" binding [here](https://github.com/Issendorff/openhab/tree/master/bundles/binding/org.openhab.binding.lcn). To not mix up both bindings I have called this binding **lcn_2**.

## Configuration in "openhab.cfg"
The following stuff has to be configured in "openhab.cfg" to make this binding run:
```
lcn_2:pchkHost=xxx.xxx.xxx.xxx
lcn_2:pchkPort=xxx
lcn_2:internalBusMonitorPort=xxx
lcn_2:pchkUsername=xxx
lcn_2:pchkPassword=xxx
```
*pchkHost*/*pchkPort* is the address of the LCN-PCHK. *pchkUsername* and *pchkPassword* illustrate the credentials to log in at the LCN-PCHK. *internalBusMonitorPort* is the port of a build-in debugging server. You can connect to this server via Telnet to monitor the binding internal LCN bus. You can set this port to 0 to avoid usage of this debugging server.

## Item configurations
The following chapters show some examples of possible item configurations. Besides usage of *module=xxx* it is also allowed to use *group=xxx*. But this is NOT allowed for stateful items! To define the target segment you can add *segment=xxx* to the item configurations ...

### Stateful items
The following items are stateful. This means that they always hold the current state of the representing unit (output, relais, sensor, ...). The states which are delivered from the LCN-bus are posted to these items. When an item is changed in openHAB, the state will be forwarded to the LCN-bus. Commands which are sent to a sensor are not forwarded to the LCN-bus!

The states of these items are updated from time to time automatically ...

#### Output
```
Number Light1Ramp { lcn_2="module=157, unit=outputRamp1" }
Dimmer Light1     { lcn_2="module=157, unit=output1" }
Switch Light2     { lcn_2="module=157, unit=output2" }
```

#### Relais
```
Switch Light3 { lcn_2="module=207, unit=relais1" }
```

#### Small light
```
Switch LED1_O { lcn_2="module=52, unit=smallLight2, type=on"      }
Switch LED1_B { lcn_2="module=52, unit=smallLight2, type=blink"   }
Switch LED1_F { lcn_2="module=52, unit=smallLight2, type=flicker" }
```

#### Sensor

```
Contact Window1 { lcn_2="module=203, unit=sensor6" }
Switch  Motion1 { lcn_2="module=203, unit=sensor4" }
Contact Motion2 { lcn_2="module=204, unit=sensor4" }
```

#### Sum
```
Contact SumA { lcn_2="module=202, unit=sum2, logic=full" }
Switch  SumB { lcn_2="module=202, unit=sum2, logic=some" }
```

#### Temperature sensor (R1Var, R2Var)
```
Number Temperature1 "Temp 1: [%.1f °C]" { lcn_2="module=157, unit=tempVar1" }
```

#### Target temperature for regulator
```
Number TargetVal "TargVal.: [%.1f °C]" { lcn_2="module=157, unit=regulatorDesiredVal1" }
```

#### Calculation value (TVar)
```
Number Counter "Stuff: [%d]" { lcn_2="module=110, unit=calcVar" }
```

### Items which send its states periodically
These items re-send its states periodically to the LCN-bus to make sure that all units in the LCN-system will have the correct state after a power loss.

#### Light scene register set (0-9)
```
Number LightRoom1RegisterSet { lcn_2="module=157, unit=cueStateRegSet" }
```

#### regulator lock
```
Switch RegulatorLock_Room1 { lcn_2="module=203, unit=regulatorLock1, targetModule=203, targetUnit=output2" }
Switch RegulatorLock_Room2 { lcn_2="module=204, unit=regulatorLock2" }
```
When the *targetModule* and *targetUnit* are defined, this output will be set to 0% by locking the regulator.

#### button lock
```
Switch ButtonLock_Switch1 { lcn_2="module=203, unit=buttonLockA2" }
Switch ButtonLock_Switch2 { lcn_2="module=203, unit=buttonLockC7" }
```

### Commands
Commands are represented as a switch. An ON state send to that switch will execute the command. The state will automatically be reset to OFF when an acknowledgement is received from the LCN-bus. It doesn't matter if the acknowledgement is positive or negative.

#### Stop ramp for output
```
Switch Light1_RampStop { lcn_2="module=157, unit=outputRampStop1" }
```

#### Flicker an output
```
Switch Light1_FlickerA { lcn_2="module=157, unit=outputFlicker1, count=15, type=slight" }
Switch Light1_FlickerB { lcn_2="module=157, unit=outputFlicker1, count=15, type=medium" }
Switch Light1_FlickerC { lcn_2="module=157, unit=outputFlicker1, count=15, type=strong" }
Switch Light1_FlickerD { lcn_2="module=157, unit=outputFlicker1, count=15, type=terminate" }
Switch Light1_FlickerE { lcn_2="module=157, unit=outputFlicker1, count=15, type=medium, speed=slow" }
Switch Light1_FlickerF { lcn_2="module=157, unit=outputFlicker1, count=15, type=medium, speed=medium" }
Switch Light1_FlickerG { lcn_2="module=157, unit=outputFlicker1, count=15, type=medium, speed=fast" }
```

#### Light scene actions (call, save)
```
Switch CallCueState1 { lcn_2="module=157, unit=cueState1, register=7, action=call" }
Switch SaveCueState1 { lcn_2="module=157, unit=cueState1, register=7, action=save" }
```

#### Beeper
```
Switch Beeper1 { lcn_2="module=203, unit=beeper, mode= normal, beeps=3" }
Switch Beeper1 { lcn_2="module=203, unit=beeper, mode=special, beeps=7" }
```

#### Send a command to a group of relais
```
Switch SomeLights { lcn_2="module=207, unit=relaisGroup, r1=toggle, r2=on, r3=off" }
```

#### Press a button
```
Switch Button1 { lcn_2="module=157, unit=buttonPressA1_short" }
Switch Button2 { lcn_2="module=203, unit=buttonPressA1_long" }
Switch Button3 { lcn_2="module=203, unit=buttonPressA1_loose" }
Switch Button4 { lcn_2="module=203, unit=buttonPressA2_delay, entity=seconds, delay=5" }
Switch Button5 { lcn_2="module=203, unit=buttonPressA2_delay, entity=minutes, delay=20" }
Switch Button6 { lcn_2="module=203, unit=buttonPressA2_delay, entity=hours, delay=7" }
Switch Button7 { lcn_2="module=203, unit=buttonPressA2_delay, entity=days, delay=2" }
```
