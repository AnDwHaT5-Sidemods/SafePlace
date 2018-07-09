package safeplace.andwhat5.config;

import java.util.ArrayList;
import java.util.List;

public class PlayerStruc {

	public PlayerStruc()
	{
		VisitedSafePlaces = new ArrayList<>();
		UUID = null;
	}
	public String UUID;
	public List <String> VisitedSafePlaces;
}
