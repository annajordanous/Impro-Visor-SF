
Welcome to Impro-Visor (Improvisation Advisor) Version 5.15,
from Prof. Bob Keller at Harvey Mudd College, 25 April 2012.

Release notes for this version may be found at the end.

If you need help, please post to the Yahoo! impro-visor user group:

    http://launch.groups.yahoo.com/group/impro-visor/

From the group, you may also obtain The Imaginary Book which contains a
large number of chords-only leadsheets, as well as other useful
resources.

Impro-Visor is free and runs on any platform that supports Java 1.6 or
later, including:
 
    Windows (XP, 2000, Vista, 7)
    MacOSX (Snow Leopard 10.6 or later needed for Java 1.6)
    Linux 

The official information site for Impro-Visor is:

    http://www.cs.hmc.edu/~keller/jazz/improvisor

which is the same as

    http://www.impro-visor.com

The official download site for Impro-Visor is sourceForge:

    http://sourceforge.net/projects/impro-visor/

Download the installer that is provided for your platform, 
then launch the installer.

Once the program is installed, there should be a launcher

    Impro-Visor

that will run the program itself. The first time the program is run it
will set up a folder in your user home for your personal version of
various files.

On Windows, you will need to adjust MIDI settings to get sound on your 
particular system. The Impro-Visor MIDI control panel is identified by
a black circular icon (representing a MIDI connector) on the right side 
of the upper icon bar. Set it to one of:

    Microsoft GS Wavetable SW Synth  

    Microsoft MIDI Mapper
or
    some external synth.


Alternatively, you may run by double-clicking the file: 

    improvisor.jar

which is a Java archive.

You cannot start the application by clicking on individual leadsheet
files. They must be opened from within.
 
You can download the latest version of Java free from:

           http://www.java.com/download/
 
What you want is the JRE (Java Runtime Environment).


I am pleased to acknowledge contributions from the following developers:

Stephen Jones, Aaron Wolin
David Morrison, Martin Hunt, Steven Gomez
Jim Herold, Brandy McMenamy, Sayuri Soejima
Emma Carlson, Jon Gillick, Kevin Tang, Stephen Lee
Chad Waters, John Goodman, Lasconic, Ryan Wieghard,
Amos Byon, Zack Merritt, Xanda Schofield, August Toman-Yih

We hope you enjoy using the program. 

Sincerely,

Bob Keller, Impro-Visor Project Director
Professor of Computer Science
Harvey Mudd College
Claremont, CA 91711

keller@cs.hmc.edu

===============================================================================

Release notes for Impro-Visor 5.15 

Improves handling of MIDI track importing.

Improves style extraction from MIDI capabilities.

===============================================================================

Release notes for Impro-Visor 5.14 (pre-release)

Bug fix: Fixes MusicXML export that was broken in 5.12, 5.13.

Adds preliminary MIDI file import. Now a MIDI file can be loaded and played
as such. Single tracks can be imported as Impro-Visor melodies.
(We do not import an entire leadsheet with chord symbols, etc.)
Sysex events in the MIDI file are ignored.

Improves Style Extraction editor and repairs some long standing problems
in that area.

Replaces Generate button with Improvise toggle button. Now improvisation
will continue until the button is toggled a second time. Playback stop is
still through the stop button or the K key.


===============================================================================

Release notes for Impro-Visor 5.13

Bug fix: Selecting New Leadsheet (control-N) caused the program to hang.

===============================================================================

Release notes for Impro-Visor 5.12

Added volume specification options to style specifications. (Use Vnnn where
nnn is an integer between 0 and 127 to control volume in notes following
this term, up to the next V setting.)

Changed Style Editor and Piano Roll Editor to accommodate volumes.

Changed the layout of the Piano Roll Editor so that controls are at the top.

Now the percussion instrument names can be names rather than numbers as before.
Numbers are still accepted, but when style files are written, names will be
used.

Now MIDI channels can be assigned (in the Mixer panel), rather than be
confined to fixed settings (melody = 1, chords = 4, bass = 7, drums = 10).

Now there is an option to send MIDI Bank 0 Select before notes. This is
set in the MIDI Preferences panel.

Now each percussion instrument is assigned a separate MIDI track. This can
be useful if the MIDI output is used as input to a Digital Audio Workstation,
for example.

Updated style files to use names for percussion instruments, added some 
volume settings, and removed some redundant or unwanted patterns.

Fixed a problem in rendering bass lines, which was causing the bass instrument
to move out of range.

Fixed a problem with saving styles containing weights with decimal points
which would cause them to fail to load.


===============================================================================

Release notes for Impro-Visor 5.11

Fixes a bug that prevented roadmaps from opening.

===============================================================================

Release notes for Impro-Visor 5.10

-------------------------------------------------------------------------------
The Style Editor workings have been greatly improved.  Looping now works without
having to set an inter-loop delay.  Copying and pasting of large groups of 
cells is silent, as is creation of a pianoroll.

-------------------------------------------------------------------------------
The shortcut for creating a pianoroll for a column is now control-shift-click,
rather than shift-click as before.  (Shift-click is used to extend a multi-cell
selection.)

-------------------------------------------------------------------------------
A bug was fixed in style rendering for playback.  It only arose in certain 
styles, such as una-mas and senor-blues.

-------------------------------------------------------------------------------
A bug was fixed wherein MIDI input entered during count-in would cause
the program to lock up.

-------------------------------------------------------------------------------
There is a remaining problem with MIDI input.  If used for a long time, 
memory will fill up and the program will start slowing down and eventually
need to be restarted.  Usually this won't happen until after a couple of
choruses have been entered. The problem is being worked.

===============================================================================

Release notes for Impro-Visor 5.09 (pre-release for 5.10)

-------------------------------------------------------------------------------
Windows users: You may need to set your MIDI settings (identified by the
black circular icon) in Impro-Visor the first time you use this release. Use

    Microsoft GS Wavetable SW Synth  
if not using other MIDI devices. Use

    Microsoft MIDI Mapper

if using other MIDI devices. The setting

    Java Sound Synthesizer 

might not work.

Impro-Visor should remember your setting the next time you launch.

-------------------------------------------------------------------------------
Added a new "push" element to style specifications, so that a chord can be
struck before it appears in the leadsheet. The swing style is the only one
currently using this feature. The former swing style has been renamed 
swing-square-comp.

-------------------------------------------------------------------------------
The Section and Style Settings have been reworked. Now a style for any section
other than the first can be specified as "Use style of previous section".
It will appear as an asterisk in the style position. The point of this
feature is that the style of an entire leadsheet can be changed without
changing the styles of each section individually. This is important for
roadmaps, since they will tend to use more sections to indicate harmonic
phrasing.

-------------------------------------------------------------------------------
Some problems with MIDI selection have been worked out. It is believed that
changing MIDI instruments will no longer wedge the program. 

NOTE: Any MIDI instruments used must be in place and running before launching 
Impro-Visor. This includes software and hardware instruments.

-------------------------------------------------------------------------------
Preference settings have been changed to 1-click. Icons for the various
preferences are found at the right end of the icon tool bar. They are,
left to right:

    Global settings (a picture of the Earth globe)

    Leadsheet settings (a leadsheet image)

    Chorus settings(a treble clef and time signature)

    Style and section settings (stylized note symbols)

    MIDI settings (a MIDI connector cross-section)

    Contour drawing settings (a pencil)

Except for MIDI, these are the same icons as in previous versions. Once the
preference window is opened, the icons inside can be used to select the
various types of preference, as before.

-------------------------------------------------------------------------------
In the Roadmap window, the option of selecting play-on-click. This means
that clicking a brick will immediately play that brick.

-------------------------------------------------------------------------------
The number of measures per line for roadmaps is now saved with the leadsheet.
The default is 8. Currently this number can only be set by starting with a
roadmap and creating a leadsheet, or by editing the text of the leadsheet
using the textual editor (or an external editor).

-------------------------------------------------------------------------------
The small status indicator in the tool bar has been replaced with text
having green background in the uppermost menu-bar. Some improvements have
been made in the information conveyed by the status indicator.

-------------------------------------------------------------------------------
The button for toggling note beaming has been replaced with a checkbox in the
View Menu.

-------------------------------------------------------------------------------
A Recur button and Lead Beats spinner have been added next the Generate button.
We are gradually moving toward the ability to have Impro-Visor generate
choruses indefinitely. Currently this works by generating the next chorus 
just before the current chorus ends. The default setting is 1.05 beats before.
This setting is touchy, in that if it is not just right, the next chorus will
start too early or too late. The amount of beats required will depend on the
tempo and the complexity of the generating grammar.

-------------------------------------------------------------------------------
When a selection or chorus is being played, the slot construction lines are
temporarily removed. The chord symbols are still shown in red.

-------------------------------------------------------------------------------
A few new grammars have been added, including some for trading twos and eights,
with either the computer first ("My") or the player first ("Your"). I also
added a "Woody Shaw" grammar and a "Wes Montgomery" grammar derived from one
each of their respective solos. Also, there is a Chord+Approach grammar that
yields more "inside" melodies, as it does not introduce color tones 
intentionally.

-------------------------------------------------------------------------------
Impro-Visor will now remember the last grammar used, and re-open with that
grammar.

-------------------------------------------------------------------------------
Impro-Visor will now remember the last style edited, and re-open the style
editor with that style.

-------------------------------------------------------------------------------
Fixed a bug in interpreting the textual leadsheet notation, wherein multiple
dots on a note were wrongly interpreted. For example c2.. is now equivalent to
c2+4+8

-------------------------------------------------------------------------------
Fixed a bug in the style editor wherein the checkboxes were being ignored.
(The checkboxes are an indication not to save the instruments of those rows.)

-------------------------------------------------------------------------------
Fixed a bug in the lick generator wherein parameters of the grammar, such
as pitch range, were not being set unless the lick generator control panel
is opened.

-------------------------------------------------------------------------------
The recovery from a bad leadsheet file is somewhat improved. It is possible 
to escape the endless cycle caused by restarting with a bad leadsheet.

-------------------------------------------------------------------------------
The number of bars per line was increased from 15 to 64.

-------------------------------------------------------------------------------
The keyboard display, if used, now continues to update after the first
chorus.

-------------------------------------------------------------------------------

End of release notes for Impro-Visor 5.09

-------------------------------------------------------------------------------
