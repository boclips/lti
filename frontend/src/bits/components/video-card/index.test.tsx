import React from 'react';
import { render, screen} from '@testing-library/react';
import AgeRange from '@bit/boclips.dev-boclips-ui.types.age-range';
import { VideoCard } from './index';

const video = {
  id: '5c7d0619c4347d45194e0af5',
  title: 'The Psychology Thinking of Psychology - with Psychology Nisbet',
  description:
    'In a lightning tour of human reasoning, world-renowned psychologist Richard Nisbett shines a new light on the shadowy world of the way we think – and how we can make our lives, and the lives of those around us, better.\nWatch the Q&A here: https://www.youtube.com/watch?v=V2Ci4ro75fM\n\nRichard Nisbett is Theodore M. Newcomb Distinguished Professor of social psychology and co-director of the Culture and Cognition program at the University of Michigan at Ann Arbor.\n\n"The most influential thinker, in my life, has been the psychologist Richard Nisbett. He basically gave me my view of the world." –Malcolm Gladwell\n\nSubscribe for regular science videos: http://bit.ly/RiSubscRibe\n\nThe Ri is on Twitter: http://twitter.com/ri_science\nand Facebook: http://www.facebook.com/royalinstitution\nand Tumblr: http://ri-science.tumblr.com/\nOur editorial policy: http://www.rigb.org/home/editorial-policy\nSubscribe for the latest science videos: http://bit.ly/RiNewsletter',
  additionalDescription: 'This is an additional description for teachers',
  releasedOn: '2019-02-15',
  playback: {
    type: 'YOUTUBE',
    id: 'XKm4VoExc0Q',
    duration: 'PT55M45S',
  },
  subjects: [{ id: '5cb499c9fd5beb4281894552', name: 'Food and Health' }],
  badges: ['youtube'],
  legalRestrictions: '',
  ageRange: new AgeRange(3, 5),
  rating: 3.0,
  yourRating: null,
  bestFor: [{ label: 'Other' }],
  createdBy: 'The Royal Institution',
  promoted: true,
  language: null,
  attachments: [],
  contentWarnings: [],
};

describe('video card test', () => {

  it('renders the video card correctly', () => {
    // @ts-ignore
    render(<VideoCard video={video} />);

    expect(screen.getByTestId('video-card')).toBeInTheDocument();
  });

  it('renders the video card correctly in edit mode', () => {
    // @ts-ignore
    render(<VideoCard video={video} editMode />);

    expect(screen.getByTestId('input-title')).toBeInTheDocument();
    expect(screen.getByTestId('textarea-description')).toBeInTheDocument();
  });

  it('renders the youtube badge when in lti theme and video is youtube', async () => {
    // @ts-ignore
    render(<VideoCard video={video} theme="lti" />);
    expect(await screen.findByTestId('youtube-license')).toBeInTheDocument();
    expect(screen.queryByTestId('boclips-license')).toBeNull();
  });

  it('renders the licenced badge when in lti theme and video is youtube', async () => {
    const streamVideo = {
      ...video,
      playback: {
        type: 'STREAM',
        duration: 'PT55M45S',
      }
    };
    // @ts-ignore
    render(<VideoCard video={streamVideo} theme="lti" />);
    expect(await screen.findByTestId('boclips-license')).toBeInTheDocument();
    expect(screen.queryByTestId('youtube-license')).toBeNull();
  });
});
