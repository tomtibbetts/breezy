package com.windhaven_consulting.breezy.embeddedcontroller.extensions.mcp.MCP23017.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.windhaven_consulting.breezy.embeddedcontroller.BreezyPin;
import com.windhaven_consulting.breezy.embeddedcontroller.BreezyPinProperty;
import com.windhaven_consulting.breezy.embeddedcontroller.DigitalInputPin;
import com.windhaven_consulting.breezy.embeddedcontroller.DigitalOutputPin;
import com.windhaven_consulting.breezy.embeddedcontroller.PinPullResistance;
import com.windhaven_consulting.breezy.embeddedcontroller.extensions.ExtensionProvider;
import com.windhaven_consulting.breezy.embeddedcontroller.extensions.mcp.MCP23017.BreezyToMCP23017Pin;
import com.windhaven_consulting.breezy.embeddedcontroller.extensions.mcp.MCP23017.MCP23017Pin;
import com.windhaven_consulting.breezy.embeddedcontroller.impl.BreezyToPi4JPinPullResistance;
import com.windhaven_consulting.breezy.embeddedcontroller.impl.Pi4JDigitalInputPinProxyImpl;
import com.windhaven_consulting.breezy.embeddedcontroller.impl.Pi4JDigitalOutputPinProxyImpl;
import com.windhaven_consulting.breezy.embeddedcontroller.impl.Pi4JPinProxyImpl;

public class MCP23017ExtensionProviderImpl implements ExtensionProvider<DigitalOutputPin> {
	static final Logger LOG = LoggerFactory.getLogger(MCP23017ExtensionProviderImpl.class);

	private GpioController gpioController;
	private GpioPinListenerDigital gpioPinListenerDigital;
	private GpioProvider gpioProvider;

	public MCP23017ExtensionProviderImpl(GpioController gpioController, GpioProvider gpioProvider, GpioPinListenerDigital gpioPinListenerDigital) {
		this.gpioController = gpioController;
		this.gpioProvider = gpioProvider;
		this.gpioPinListenerDigital = gpioPinListenerDigital;
	}

	@Override
	public DigitalInputPin provisionDigitalInputPin(String name, String pinName, UUID pinId, PinPullResistance pinPullResistance, Integer debounce, boolean isEventTrigger) {
		BreezyPin breezyPin = MCP23017Pin.getByName(pinName);
		com.pi4j.io.gpio.Pin pi4JPin = BreezyToMCP23017Pin.getPin(breezyPin);
		com.pi4j.io.gpio.PinPullResistance pi4JPinPullResistance = BreezyToPi4JPinPullResistance.getPinPullResistance(pinPullResistance);
		GpioPinDigitalInput gpioPin = gpioController.provisionDigitalInputPin(gpioProvider, pi4JPin, pi4JPinPullResistance);
		
		gpioPin.setDebounce(debounce);
		gpioPin.setProperty(BreezyPinProperty.NAME.name(), name);
		gpioPin.setProperty(BreezyPinProperty.ID.name(), pinId.toString());
		
		if(isEventTrigger) {
			gpioPin.addListener(gpioPinListenerDigital);
		}
		
		return new Pi4JDigitalInputPinProxyImpl(name, pinId, gpioPin);
	}

	@Override
	public DigitalOutputPin provisionOutputPin(String name, String pinName, UUID pinId) {
		BreezyPin breezyPin = MCP23017Pin.getByName(pinName);
		com.pi4j.io.gpio.Pin pi4JPin = BreezyToMCP23017Pin.getPin(breezyPin);
		GpioPinDigitalOutput gpioPin = gpioController.provisionDigitalOutputPin(gpioProvider, pi4JPin, PinState.LOW);
		
		gpioPin.setProperty(BreezyPinProperty.NAME.name(), name);
		gpioPin.setProperty(BreezyPinProperty.ID.name(), pinId.toString());
		
		return new Pi4JDigitalOutputPinProxyImpl(name, pinId, gpioPin);
	}

	@Override
	public void unprovisionPin(BreezyPin breezyPin) {
		Pi4JPinProxyImpl pi4jPinProxyImpl = (Pi4JPinProxyImpl) breezyPin;
		gpioController.unprovisionPin(pi4jPinProxyImpl.getGpioPin());
	}
}
