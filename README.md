# CS141-Robocode-TeamMrRobot

Robot Concept: Attacks the lowest health bot while avoiding the ones with high health. When only 1 or 2 robots in the field, implement melee strategy, using Minimum Risk movement to avoid walls/bots. Ram other bots when close.

To Do: //comment on whatever you think you can help with most
1. Sensing - implement radar techniques to track other robots, remember that when gun moves radar moves unless specified otherwise
    --Includes tracking when a bullet has been fired by opponent and how many enemies are on the field
2. Shooting/Attacking - controls gun and melee tactics
3. Moving/Dodging - movement algorithm based on minimum risk movement //JAY can handle this
    --Includes tracking own position on battlefield - part of Sensing code likely (onScannedRobot method)
