package jvstmresults;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

class DataPoint
	{
		public int x;
		public int y;
		public double value;
		public boolean isDummy;
		public static final double dummy = 0.0;
		public static final DecimalFormat df = new DecimalFormat("#.#");

		static
		{
			df.setRoundingMode(RoundingMode.DOWN);
			DecimalFormatSymbols s = DecimalFormatSymbols.getInstance();
			s.setDecimalSeparator('.');
			df.setDecimalFormatSymbols(s);
		}

		public DataPoint(int x, int y, double value)
		{
			super();
			this.x = x;
			this.y = y;
			this.value = value;
			if (value < 1.0)
			{
				isDummy = true;
			} else
			{
				isDummy = false;
			}
		}

		@Override
		public String toString()
		{
			if (isDummy)
			{
				return x + " " + y + " " + "0";
			}

			return x + " " + y + " " + df.format(value);
		}

		public String toString(int position)
		{
			if (isDummy)
			{
				return position + " " + x + "x" + y + " " + "0";
			}

			return position + " " + x + "x" + y + " " + df.format(value);
		}
	}
