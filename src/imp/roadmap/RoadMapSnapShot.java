/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.roadmap;

/**
 *
 * @author ImproVisor
 */
public class RoadMapSnapShot {
    private String name;
    private RoadMap roadMap = new RoadMap();
    
    public RoadMapSnapShot(String name, RoadMap roadMap)
    {
        this.name = name;
        this.roadMap = new RoadMap(roadMap);
    }
    
    public RoadMap getRoadMap()
    {
        return roadMap;
    }
    
    public String getName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
