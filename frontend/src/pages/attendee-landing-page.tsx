import { useAuth } from "react-oidc-context";
import { Button } from "../components/ui/button";
import { useNavigate } from "react-router";
import { Input } from "@/components/ui/input";
import { AlertCircle, Search } from "lucide-react";
import { useEffect, useState } from "react";
import { PublishedEventSummary, SpringBootPagination } from "@/domain/domain";
import { listPublishedEvents, searchPublishedEvents } from "@/lib/api";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import PublishedEventCard from "@/components/published-event-card";
import { SimplePagination } from "@/components/simple-pagination";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

const AttendeeLandingPage: React.FC = () => {
  const { isAuthenticated, isLoading, signinRedirect, signoutRedirect } =
    useAuth();

  const navigate = useNavigate();

  const [page, setPage] = useState(0);
  const [publishedEvents, setPublishedEvents] = useState<
    SpringBootPagination<PublishedEventSummary> | undefined
  >();
  const [error, setError] = useState<string | undefined>();
  const [query, setQuery] = useState<string | undefined>();

  // popup state
  const [showDemoAccounts, setShowDemoAccounts] = useState(false);

  const demoAccounts = [
    { username: "attendee", password: "attendee" },
    { username: "organizer", password: "organizer" },
    { username: "staff", password: "staff" },
  ];

  useEffect(() => {
    if (query && query.length > 0) {
      queryPublishedEvents();
    } else {
      refreshPublishedEvents();
    }
  }, [page]);

  useEffect(() => {
    // show demo accounts popup once on load
    setShowDemoAccounts(true);
  }, []);

  const refreshPublishedEvents = async () => {
    try {
      setPublishedEvents(await listPublishedEvents(page));
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else if (typeof err === "string") {
        setError(err);
      } else {
        setError("An unknown error has occurred");
      }
    }
  };

  const queryPublishedEvents = async () => {
    if (!query) {
      await refreshPublishedEvents();
    }

    try {
      setPublishedEvents(await searchPublishedEvents(query ?? "", page));
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else if (typeof err === "string") {
        setError(err);
      } else {
        setError("An unknown error has occurred");
      }
    }
  };

  if (error) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-black via-gray-900 to-black text-white flex items-center justify-center">
        <Alert variant="destructive" className="bg-gray-900 border-red-700 max-w-lg rounded-xl shadow-lg">
          <AlertCircle className="h-5 w-5" />
          <AlertTitle className="font-semibold">Error</AlertTitle>
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      </div>
    );
  }

  if (isLoading) {
    return <p className="text-center mt-20 text-gray-300">Loading...</p>;
  }

  return (
    <div className="bg-gradient-to-b from-black via-gray-950 to-black min-h-screen text-white">
      {/* Nav */}
      <div className="flex justify-end p-6 container mx-auto">
        {isAuthenticated ? (
          <div className="flex gap-3">
            <Button
              onClick={() => navigate("/dashboard")}
              className="cursor-pointer bg-gradient-to-r from-indigo-500 to-purple-600 text-white shadow-lg hover:opacity-90"
            >
              Dashboard
            </Button>
            <Button
              className="cursor-pointer bg-gray-800 hover:bg-gray-700 border border-gray-700"
              onClick={() => signoutRedirect()}
            >
              Log out
            </Button>
          </div>
        ) : (
          <Button
            className="cursor-pointer bg-gradient-to-r from-blue-500 to-indigo-600 text-white shadow-lg hover:opacity-90"
            onClick={() => signinRedirect()}
          >
            Log in
          </Button>
        )}
      </div>

      {/* Hero */}
      <div className="container mx-auto px-6 mb-10">
        <div className="bg-[url(/organizers-landing-hero.png)] bg-cover min-h-[180px] md:min-h-[220px] rounded-2xl shadow-2xl bg-center overflow-hidden relative">
          <div className="absolute inset-0 bg-black/60" />
          <div className="relative z-10 p-6 md:p-10">
            <h1 className="text-2xl md:text-4xl font-extrabold mb-4 leading-snug">
              Discover and Book Tickets for Exciting Events
            </h1>
            <div className="flex gap-3 max-w-xl">
              <Input
                className="bg-white text-black rounded-lg shadow-md"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Search for events..."
              />
              <Button
                onClick={queryPublishedEvents}
                className="bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg shadow-md"
              >
                <Search className="h-5 w-5" />
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Published Event Cards */}
      <div className="container mx-auto px-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {publishedEvents?.content?.map((publishedEvent) => (
          <PublishedEventCard
            publishedEvent={publishedEvent}
            key={publishedEvent.id}
          />
        ))}
      </div>

      {publishedEvents && (
        <div className="w-full flex justify-center py-10">
          <SimplePagination
            pagination={publishedEvents}
            onPageChange={setPage}
          />
        </div>
      )}

      {/* Auto Demo Accounts Modal */}
      <Dialog open={showDemoAccounts} onOpenChange={setShowDemoAccounts}>
        <DialogContent className="bg-gradient-to-br from-gray-900 to-black text-white border border-gray-800 rounded-2xl shadow-2xl max-w-md">
          <DialogHeader>
            <DialogTitle className="text-2xl font-bold text-center">
              Demo Accounts
            </DialogTitle>
          </DialogHeader>
          <p className="text-sm mb-4 text-gray-400 text-center">
            Use these demo accounts to explore the platform.
          </p>

          {/* Sub-heading for clarity */}
          <p className="text-xs uppercase tracking-wide text-gray-500 mb-3 text-center">
            Usernames & Passwords
          </p>

          <div className="space-y-3">
            {demoAccounts.map((account, idx) => (
              <div
                key={idx}
                className="flex justify-between items-center px-4 py-2 bg-gray-800 rounded-lg hover:bg-gray-700 transition"
              >
                <span className="font-mono">{account.username}</span>
                <span className="font-mono text-gray-400">{account.password}</span>
              </div>
            ))}
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default AttendeeLandingPage;
