/*
 * 
 */
package imp.gui;

import imp.util.MidiManager;
import java.util.LinkedHashSet;
import javax.sound.midi.MidiDevice;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * MIDI device chooser, for preferences window within Notate
 * @author Martin Hunt
 */

public class MidiDeviceChooser
    extends AbstractListModel
    implements ComboBoxModel
{
private MidiManager midiManager;

private LinkedHashSet<MidiDevice.Info> devices;

private Object selectedItem = null;

public MidiDeviceChooser(MidiManager midiManager, LinkedHashSet<MidiDevice.Info> devices)
  {
    this.midiManager = midiManager;
    this.devices = devices;
  }

public int getSize()
  {
    return devices.size();
  }

public Object getElementAt(int index)
  {
   Object array[] = devices.toArray();
   MidiDevice.Info o = (MidiDevice.Info)array[index];

    if( o == null )
      {
        return midiManager.defaultDeviceLabel;
      }
    return o;
  }

public void setSelectedItem(Object anItem)
  {
    selectedItem = anItem;
  }

public Object getSelectedItem()
  {
    return selectedItem;
  }
            
}
