package minecraftwl.MCBT;

public class MCBTCount {
	
	public Long blocksBroken;
	public Long blocksBurned;
	public Long blocksExploded;
	public Long blocksPlaced;
	
	public MCBTCount(Long _blocksExploded, Long _blocksBroken, Long _blocksPlaced, Long _blocksBurned) {
     
		blocksBroken   = _blocksBroken;
		blocksBurned   = _blocksBurned;
		blocksExploded = _blocksExploded;
		blocksPlaced   = _blocksPlaced;
	}

	public String toString() {
		return blocksBroken.toString()  + " blocks broken, " +
			   blocksBurned + " blocks burned, " +
			   blocksExploded.toString() + " blocks exploded, and " + 
			   blocksPlaced + " blocks placed.";
	}
	
}
