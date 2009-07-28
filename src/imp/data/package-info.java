/**
 * Data structures and methods for storing and playing parallel melodies
 * and chord progressions.
 * The main class here is Score.  A Score can contain several Parts.
 * A Part has a Vector of slots, each of which can hold a Unit.  A
 * Unit is just an interface.  Chord, Note, and Rest are all Units.
 *
 * After creating a Score, a Part or several Parts must be added.
 * A Part starts out with one long Rest, starting from its first 
 * slot.
 *
 * After creating a Part, Chords, Notes, or Rests can be inserted into
 * the slots.  (A slot is just the start of a Unit, the rhythm value
 * will continue until a non-empty slot is reached.)
 *
 * A Score can be played by creating a MidiSynth object and calling
 * its play function on the Score.
 */

package imp.data;
