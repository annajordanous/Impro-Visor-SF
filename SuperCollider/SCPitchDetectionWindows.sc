/**
* This Java Class is part of the Impro-Visor Application
*
* Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
*
* Impro-Visor is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* Impro-Visor is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* merchantability or fitness for a particular purpose.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Impro-Visor; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
* Class to capture and interpret audio input. Uses the built in 'Tartini' feature
* (which in turn uses the McLeod Pitch Method) to determine pitch.
*
* @author Anna Turner
* @author SuperCollider 3 documentation contributors
* (http://doc.sccode.org/Other/HelpDocsLicensing.html)
* @author Nick Collins (specifically chapter 15, section 15.3.3,
* figure 15.7 in the SuperCollider book)
*
* @version 5.17
* @since June 21 2013
*/

(
var highThreshold = 95;//Default
var lowThreshold = 43;//Default
var lowSlider, highSlider, lowTextVal, highTextVal;
var lowDesc, highDesc;
var pitchAndOnsets = \pitchAndOnsets;
var lastTime, lastPitch, duration, started=false;//Note recognition variables
~restCheckOn = true; //Monitors trigger response

/*Set up server stuff*/
Server.default = s = Server.internal.boot;


/*Handle GUI, create pitch/rest recognition, run.*/
s.doWhenBooted({

	//GUI
    /*
	 * This section handles the user-input for accepted note ranges.
	 *
	 * A small window pops up, allowing the user to choose what the highest
	 * and lowest notes registered by improvisor are. Done via sliders.
	 *
	 */

	//Set to Qt for platform-independence
	GUI.qt;

	//Create controlSpec
	c = [20, 120, 'lin', 1, 20].asSpec;

	//Create window
	w = Window.new("Set Note Ranges: MIDI", Rect(500,500,490,160));

	//Create sliders and associated text
	lowSlider = Slider.new(w, Rect(20, 30, 200, 30));
	lowTextVal = StaticText.new(w, Rect(10, 3, 300, 30));
	lowTextVal.string = "43";//Default
	lowSlider.value = (lowTextVal.string.asInt-20)/100;//Linearly scaling
	lowSlider.action = {//Update vals, don't allow crossing over highSlider value
		lowTextVal.string = c.map(lowSlider.value);
		if(lowTextVal.string.asInt >= highTextVal.string.asInt)
		{
			highTextVal.string = c.map(lowSlider.value);
			highSlider.value = (highTextVal.string.asInt-20)/100;
		};
		lowThreshold = lowTextVal.string.asInt;//Thresh
	};

	highSlider = Slider.new(w, Rect(270, 30, 200, 30));
	highTextVal = StaticText.new(w, Rect(270, 3, 300, 30));
	highTextVal.string = "95";//Default
	highSlider.value = (highTextVal.string.asInt - 20)/100;//Lin scaling
	highSlider.action = {//Update vals, don't allow crossing over lowSlider value
		highTextVal.string = c.map(highSlider.value);
		if(highTextVal.string.asInt <= lowTextVal.string.asInt){
			lowTextVal.string = c.map(highSlider.value);
			lowSlider.value = (lowTextVal.string.asInt-20)/100;
		};
		highThreshold = highTextVal.string.asInt;//Thresh
	};

	//Create text labels
	lowDesc = StaticText(w, Rect(20, 60, 140, 30));
	lowDesc.string = "Adjust lowest possible MIDI note";

	highDesc = StaticText(w, Rect(270, 60, 140, 30));
	highDesc.string = "Adjust highest possible MIDI note";

	//Create button
	b = Button.new(w,Rect(190,110,110,30)).states_([["Set Note Range"]]);

	//Set button functionality
	b.action = {
		w.close;
		//"Low thresh: ".post;
		//lowThreshold.postln;
		//"Hi thresh: ".post;
		//highThreshold.postln;
	};

	w.front;


	//NOTES
	w.onClose_({

		Routine.run({
			//Allocate buffer space.
			b = Buffer.alloc(s, 512);

			/*Checks onset of notes,rests; sends respective triggers.*/
			SynthDef(\pitchAndOnsets, {

				var in, freq, hasFreq, chain, onsets;
				var trigger, restTrig;

				in = SoundIn.ar(0);
				# freq, hasFreq = Tartini.kr(in);
				//freq.poll;

				chain = FFT(b, in);
				onsets = Onsets.kr(chain, 0.9, \rcomplex);

				//COULD DO: only if hasFreq > 0.9 then execute following line
				trigger = SendTrig.kr(TDelay.kr(onsets, 0.1), 1, freq);

				restTrig = freq<110;//All notes below 110 qualify as rest
				SendTrig.kr(restTrig,2,0);

			}).add;

			2.wait;//To allow time for synth to register with server

			x = Synth(\pitchAndOnsets);

			/*Handle Note List Input*/

			//MIDIOut setup
			MIDIClient.list;
			~outports = MIDIClient.destinations.size;
			MIDIClient.init;
			//~m_out_server = MIDIOut(1, MIDIClient.destinations.at(1).uid);
			~m_out_server = MIDIOut.newByName("LoopBe Internal MIDI", "LoopBe Internal MIDI", true);
			~m_out_server.latency = 0;

			//Create Responder
			o = OSCresponder(s.addr,'/tr',{ arg time,responder,msg;
				var newNote;
				var midiNote;

				if(started,{
					case
					//Rest Trigger
					{msg[2]==2}
					{
						if(~restCheckOn)//If not chaining rests together
						{
							~m_out_server.noteOff(1, lastPitch, 0);///last played note off
							~restCheckOn = false;//Stop rest trig til note trig activated
						}
					}
					//Onset Trigger
					{msg[2]==1}
					{
						if(~restCheckOn){//If previous note was not rest
							~m_out_server.noteOff(1, lastPitch, 0);//Turn off last note
						};
						//Else, simple noteOn:
						if((msg[3].cpsmidi<= highThreshold) &&
							(msg[3].cpsmidi>= lowThreshold)){
							//"Note struck at time: ".post;
							//time.post;
							//" freq: ".post;
							//(msg[3].cpsmidi).postln;
							lastPitch = (msg[3]).cpsmidi.round(1);//Reset pitch
							~m_out_server.noteOn(1, lastPitch, 64);
							~restCheckOn = true;//Ok to start rests again
						};
					}
					},{
						started = true;
						lastPitch=0;
				});//End if

				if(~restCheckOn){
					lastTime = time;//Reset time, if not chaining rests
				}

				}//End Function
			).add;//End Responder
		});//End routine
	});//End onClose

})//End doWhenBooted
)//End server asgt block




