// import { ChevronLeft, ChevronRight } from "lucide-react";
import { DayPicker, type DayPickerProps } from "react-day-picker";
import "react-day-picker/dist/style.css"; // Default styles

import { cn } from "@/lib/utils";
import { buttonVariants } from "@/components/ui/button";

function Calendar({
  className,
  classNames,
  showOutsideDays = true,
  ...props
}: DayPickerProps) {
  return (
    <DayPicker
      showOutsideDays={showOutsideDays}
      className={cn(
        "p-4 bg-white dark:bg-gray-950 rounded-lg shadow-md w-full",
        className
      )}
      classNames={{
        months: "flex justify-center",
        month: "space-y-4",
        caption: "flex justify-between items-center px-2",
        caption_label: "text-base font-semibold",
        nav: "flex items-center gap-1",
        nav_button: cn(
          buttonVariants({ variant: "ghost" }),
          "h-8 w-8 p-0 opacity-70 hover:opacity-100"
        ),
        table: "w-full border-collapse",
        head_row: "flex",
        head_cell:
          "w-10 text-center text-sm text-gray-500 dark:text-gray-400",
        row: "flex w-full",
        cell:
          "w-10 h-10 text-center text-sm flex items-center justify-center relative",
        day: "w-full h-full rounded-full hover:bg-gray-200 dark:hover:bg-gray-800 focus:outline-none",
        day_selected: "bg-blue-600 text-white hover:bg-blue-700",
        day_today: "border border-blue-500 text-blue-600 font-medium",
        day_outside: "text-gray-400 opacity-50",
        day_disabled: "text-gray-300 opacity-50",
        day_hidden: "invisible",
        ...classNames,
      }}
      {...props}
    />
  );
}

export { Calendar };
