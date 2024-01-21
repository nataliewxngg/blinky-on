BLINKY-ON
Natalie Wong
January 21, 2024

Blinky-ON is a dynamic, pausable single player game that utilizes the 
arrow keys to navigate a high-speed car through busy one-way traffic.

HINTS:
- Leave extra space between the corners of the cars when maneuvering to avoid 
  collision! (due to image's width and height)
- Utilizing a car with a higher steering speed is not always better!
- In similarity to reality, cars may suddenly slow down to compensate for 
  a slower car in front of it. Drive CAREFULLY and beware of this!
- The DASH powerup may be beneficial but risky! 
  A vehicle may be in front of you but out of your line of sight - Use at your own risk! 
  (NOT A "BUG MADE FEATURE"!!!! - the DASH powerup would have been too overpowered without it)
- Try to go FAST so that cars won't cluster at the top!

FUNCTIONALITIES MISSING FROM INITIAL PROPOSAL: NONE!!!!!!

ADDITIONAL FUNCTIONALITIES:
- Implementation of varying steering speeds (in the store)! I originally intended to 
  incorporate different car colors/skins only.
- Implementation of the toggleable hitbox and SFX settings! 
  (and the entire settings menu - I only realized one day before the deadline that 
  I promised to incorporate this in my initial proposal...)
- All-inclusive sound effects!

KNOWN BUGS/ERRORS:
- Enemy cars may flicker from time to time. 
  Fortunately, this does NOT affect the overall execution of the game and is hardly
  noticeable.
- When the game initially launches, the player's Car object will be null for a 
  VERY SHORT period of time (a few ms) which will consequently throw a NullPointerException. 
  (does not affect the game in any aspect though) 
- The collision detection is sometimes off due to the inaccurate hitbox at 
  the corners of the cars. (due to image's dimensions) 
  This was moreover noted and acknowledged in my initial design. (privately commented)

IMPORTANT INFORMATION:
- Be sure to TAP the up/down arrow keys to speed up/slow down rather than HOLDING them!
- ALL keybinds are currently allowed in the settings, meaning that all 4 controls can share
  the same keys. Try to avoid this! (an area of improvement I will work on in the future)
- Have fun :)
