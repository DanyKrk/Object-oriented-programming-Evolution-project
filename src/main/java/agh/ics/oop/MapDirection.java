package agh.ics.oop;

public enum MapDirection {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NORTHEAST,
    SOUTHEAST,
    SOUTHWEST,
    NORTHWEST;

    public String toString(){
        switch(this){
            case NORTH: return "N";
            case SOUTH: return "S";
            case EAST: return "E";
            case WEST: return "W";
            case NORTHEAST: return "NE";
            case NORTHWEST: return "NW";
            case SOUTHEAST: return "SE";
            case SOUTHWEST: return "SW";
        }
        return"No toString case!";
    }

    public MapDirection next(){
        switch(this){
            case EAST: return SOUTHEAST;
            case SOUTHEAST: return SOUTH;
            case SOUTH: return SOUTHWEST;
            case SOUTHWEST: return WEST;
            case WEST: return NORTHWEST;
            case NORTHWEST: return NORTH;
            case NORTH: return NORTHEAST;
            case NORTHEAST: return EAST;
        }
        return NORTH;
    }

    public MapDirection previous(){
        switch(this){
            case EAST: return NORTHEAST;
            case NORTHEAST: return NORTH;
            case NORTH: return NORTHWEST;
            case NORTHWEST: return WEST;
            case WEST: return SOUTHWEST;
            case SOUTHWEST: return SOUTH;
            case SOUTH: return SOUTHEAST;
            case SOUTHEAST: return EAST;
        }
        return NORTH;
    }

    public Vector2d toUnitVector(){
        switch(this){
            case EAST: return new Vector2d(1,0);
            case SOUTHEAST: return new Vector2d(1, -1);
            case SOUTH: return new Vector2d(0,-1);
            case SOUTHWEST: return new Vector2d(-1, -1);
            case WEST: return new Vector2d(-1,0);
            case NORTHWEST: return new Vector2d(-1, 1);
            case NORTH: return new Vector2d(0,1);
            case NORTHEAST: return new Vector2d(1,1);
            default: return new Vector2d(0,0);
        }
    }
}
