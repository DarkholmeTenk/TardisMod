package tardis.common.tileents.extensions;

public enum LabFlag
{
	NOTINFLIGHT, //Recipe will only run while TARDIS is not in flight.
	INFLIGHT, //Recipe will only run while TARDIS is in flight (any flight)
	INCOORDINATEDFLIGHT,
	INUNCOORDINATEDFLIGHT; // Recipe will only run while TARDIS is in uncoordinated flight.
}
