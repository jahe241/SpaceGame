import os
from PIL import Image, ImageFont, ImageDraw
import string

problematic_chars = {
    "&": "ampersand",
    "*": "asterisk",
    "@": "at",
    "\\": "backslash",
    ":": "colon",
    ",": "comma",
    "$": "dollar",
    "=": "equals",
    "!": "exclamation",
    ">": "greater_than",
    "<": "less_than",
    "-": "minus",
    "%": "percent",
    ".": "period",
    "+": "plus",
    "?": "question",
    ";": "semicolon",
    "/": "slash",
    "^": "caret",
    "~": "tilde",
    "_": "underscore",
    "|": "vertical_bar",
    "#": "hash",
    "{": "left_curly_brace",
    "}": "right_curly_brace",
    "[": "left_square_bracket",
    "]": "right_square_bracket",
    "(": "left_parenthesis",
    ")": "right_parenthesis",
    '"': "quote",
    "'": "apostrophe",
    "`": "backtick",
}


def slice_font(font_path, font_size, image_size, output_dir):
    # Load the font
    font = ImageFont.truetype(font_path, font_size)

    # List of characters in the font
    characters = string.ascii_letters + string.digits + string.punctuation

    # Create the output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)

    # Iterate over the characters
    # Iterate over the characters
    for char in characters:
        # Create a new image for the character with a transparent background
        image = Image.new("RGBA", image_size, (0, 0, 0, 0))

        # Create a draw object
        d = ImageDraw.Draw(image)

        # Draw the character off-screen to get its size
        offscreen = Image.new("RGBA", (1, 1))
        offscreen_draw = ImageDraw.Draw(offscreen)
        w, h = offscreen_draw.textbbox((0, 0), char, font=font)[2:]

        # Calculate the position of the text to align it to the left, center of the image
        x = 1  # or a small padding value if you want some space on the left
        y = (image_size[1] - h) / 2

        # Draw the character in white
        d.text((x, y), char, font=font, fill=(255, 255, 255, 255))

        # Check if the character is uppercase and prepend 'c' to the filename
        filename_char = "c" + char if char.isupper() else char

        # Check if the character is problematic and replace it if necessary
        filename_char = problematic_chars.get(filename_char, filename_char)

        # Save the image as a separate PNG in the output directory
        image.save(os.path.join(output_dir, f"joystix_{filename_char}.png"))


# Usage
slice_font("joystix monospace.otf", 32, (32, 32), "output")
